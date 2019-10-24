package com.databasepreservation.desktop.client.dbptk.wizard.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.databasepreservation.common.client.BrowserService;
import com.databasepreservation.common.shared.ViewerConstants;
import com.databasepreservation.common.shared.ViewerStructure.IsIndexed;
import com.databasepreservation.common.shared.ViewerStructure.ViewerDatabase;
import com.databasepreservation.common.shared.client.breadcrumb.BreadcrumbItem;
import com.databasepreservation.common.shared.client.breadcrumb.BreadcrumbPanel;
import com.databasepreservation.common.shared.client.common.DefaultAsyncCallback;
import com.databasepreservation.common.shared.client.tools.BreadcrumbManager;
import com.databasepreservation.common.shared.client.tools.HistoryManager;
import com.databasepreservation.common.shared.client.tools.ToolkitModuleName2ViewerModuleName;
import com.databasepreservation.common.shared.models.wizardParameters.ConnectionParameters;
import com.databasepreservation.common.shared.models.wizardParameters.ExportOptionsParameters;
import com.databasepreservation.common.shared.client.common.dialogs.Dialogs;
import com.databasepreservation.desktop.client.dbptk.wizard.WizardManager;
import com.databasepreservation.desktop.client.dbptk.wizard.WizardPanel;
import com.databasepreservation.desktop.client.dbptk.wizard.common.connection.Connection;
import com.databasepreservation.desktop.client.dbptk.wizard.common.exportOptions.MetadataExportOptions;
import com.databasepreservation.common.shared.client.common.visualization.browse.wizard.common.progressBar.ProgressBarPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import config.i18n.client.ClientMessages;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public class DBMSWizardManager extends WizardManager {
  @UiField
  public ClientMessages messages = GWT.create(ClientMessages.class);

  interface DBMSWizardManagerUiBinder extends UiBinder<Widget, DBMSWizardManager> {
  }

  private static DBMSWizardManagerUiBinder binder = GWT.create(DBMSWizardManagerUiBinder.class);

  @UiField
  BreadcrumbPanel breadcrumb;

  @UiField
  FlowPanel wizardContent, customButtons;

  @UiField
  Button btnNext, btnCancel, btnBack;

  private static HashMap<String, DBMSWizardManager> instances = new HashMap<>();
  private ArrayList<WizardPanel> wizardInstances = new ArrayList<>();
  private final String databaseUUID;
  private String databaseName;

  private ViewerDatabase database;
  private int position = 0;
  private final int positions = 0;

  private ConnectionParameters connectionParameters;
  private ExportOptionsParameters exportOptionsParameters;

  public static DBMSWizardManager getInstance(String databaseUUID) {
    if (instances.get(databaseUUID) == null) {
      instances.put(databaseUUID, new DBMSWizardManager(databaseUUID));
      instances.get(databaseUUID).init();
    }

    return instances.get(databaseUUID);
  }

  private void init() {
    wizardContent.clear();
    Connection connection = Connection.getInstance(databaseUUID);
    connection.initExportDBMS(ViewerConstants.DOWNLOAD_WIZARD_MANAGER, HistoryManager.ROUTE_SEND_TO_LIVE_DBMS);
    wizardInstances.add(0, connection);
    wizardContent.add(connection);
    btnBack.setVisible(false);
    btnNext.setEnabled(false);
    btnNext.setText(messages.wizardSendToDBMSExportButton());
    updateButtons();
    updateBreadcrumb();

  }

  public void setBreadcrumbDatabaseName(final String databaseName) {
    this.databaseName = databaseName;
    updateBreadcrumb();
  }

  private DBMSWizardManager(String databaseUUID) {
    initWidget(binder.createAndBindUi(this));

    this.databaseUUID = databaseUUID;

    btnNext.addClickHandler(event -> {
      handleWizard();
    });

    btnCancel.addClickHandler(event -> {
      clear();
      for (WizardPanel wizardPanel : wizardInstances) {
        wizardPanel.clear();
      }
      instances.clear();
      HistoryManager.gotoSIARDInfo(databaseUUID);
    });
  }

  @Override
  public void enableNext(boolean value) {
    btnNext.setEnabled(value);
  }

  @Override
  protected void handleWizard() {
    GWT.log("Position: " + position);
    switch (position) {
      case 0:
        handleDBMSConnection();
        break;
      case 1:
        handleSIARDExportOptions();
        break;
    }
  }

  private void handleSIARDExportOptions() {
    final boolean valid = wizardInstances.get(position).validate();
    if (valid) {
      exportOptionsParameters = (ExportOptionsParameters) wizardInstances.get(position).getValues();
      wizardContent.clear();
      MetadataExportOptions metadataExportOptions = MetadataExportOptions
        .getInstance(exportOptionsParameters.getSIARDVersion(), false);
      wizardInstances.add(position, metadataExportOptions);
      wizardContent.add(metadataExportOptions);
      updateButtons();
      updateBreadcrumb();
    } else {
      wizardInstances.get(position).error();
    }
  }

  private void handleDBMSConnection() {
    final boolean valid = wizardInstances.get(position).validate();
    if (valid) {
      connectionParameters = (ConnectionParameters) wizardInstances.get(position).getValues();
      migrateToDBMS();
    } else {
      wizardInstances.get(position).error();
    }
  }

  private void migrateToDBMS() {
    wizardContent.clear();
    enableButtons(false);
    position = 3;
    updateBreadcrumb();

    BrowserService.Util.getInstance().retrieve(databaseUUID, ViewerDatabase.class.getName(), databaseUUID,
      new DefaultAsyncCallback<IsIndexed>() {
        @Override
        public void onSuccess(IsIndexed result) {
          database = (ViewerDatabase) result;
          final String siardPath = database.getSIARDPath();

          ProgressBarPanel progressBarPanel = ProgressBarPanel.getInstance(databaseUUID);
          progressBarPanel.setTitleText(messages.progressBarPanelTextForDBMSWizardTitle(
            ToolkitModuleName2ViewerModuleName.transform(connectionParameters.getModuleName())));
          progressBarPanel.setSubtitleText(messages.progressBarPanelTextForDBMSWizardSubTitle());
          wizardContent.add(progressBarPanel);

          BrowserService.Util.getInstance().migrateToDBMS(databaseUUID, database.getSIARDVersion(), siardPath,
            connectionParameters,
            new DefaultAsyncCallback<Boolean>() {

              @Override
              public void onFailure(Throwable caught) {
                wizardContent.clear();
                position = 0;
                wizardContent.add(wizardInstances.get(position));
                enableButtons(true);
                updateBreadcrumb();
                enableNext(false);

                Dialogs.showErrors(messages.errorMessagesConnectionTitle(), caught.getMessage(),
                  messages.basicActionClose());
              }

              @Override
              public void onSuccess(Boolean result) {
                if (result) {
                  Dialogs.showInformationDialog(messages.sendToWizardManagerInformationTitle(),
                    messages.sendToWizardManagerInformationMessageDBMS(
                      connectionParameters.getJDBCConnectionParameters().getConnection().get("database")),
                    messages.basicActionClose(), "btn btn-link", new DefaultAsyncCallback<Void>() {
                      @Override
                      public void onSuccess(Void result) {
                        clear();
                        for (WizardPanel wizardPanel : wizardInstances) {
                          wizardPanel.clear();
                        }
                        instances.clear();
                        HistoryManager.gotoSIARDInfo(databaseUUID);
                      }
                    });
                }
              }
            });
        }
      });
  }

  @Override
  protected void updateButtons() {
  }

  @Override
  protected void enableButtons(boolean value) {
    btnCancel.setEnabled(value);
    btnNext.setEnabled(value);
    btnBack.setEnabled(value);
  }

  @Override
  protected void updateBreadcrumb() {
    List<BreadcrumbItem> breadcrumbItems;

    switch (position) {
      case 0:
        breadcrumbItems = BreadcrumbManager.forDBMSConnectionSendToWM(databaseUUID, databaseName);
        break;
      case 1:
        breadcrumbItems = BreadcrumbManager.forSIARDExportOptionsSenToWM(databaseUUID, databaseName);
        break;
      case 2:
        breadcrumbItems = BreadcrumbManager.forMetadataExportOptionsSendToWM(databaseUUID, databaseName);
        break;
      case 3:
        breadcrumbItems = BreadcrumbManager.forProgressBarPanelSendToWM(databaseUUID, databaseName);
        break;
      default:
        breadcrumbItems = new ArrayList<>();
        break;
    }

    BreadcrumbManager.updateBreadcrumb(breadcrumb, breadcrumbItems);
  }

  @Override
  protected void clear() {
    super.clear();

    ProgressBarPanel progressBarPanel = ProgressBarPanel.getInstance(databaseUUID);
    progressBarPanel.clear(databaseUUID);
  }

  public void change(String wizardPage, String toSelect) {
    internalChanger(wizardPage, toSelect);
  }

  private void internalChanger(String wizardPage, String toSelect) {
    WizardPanel wizardPanel = wizardInstances.get(position);
    if (HistoryManager.ROUTE_WIZARD_CONNECTION.equals(wizardPage)) {
      if (wizardPanel instanceof Connection) {
        Connection connection = (Connection) wizardPanel;
        connection.sideBarHighlighter(toSelect);
      } else {
        HistoryManager.gotoSendToLiveDBMS(databaseUUID);
      }
    } else {
      HistoryManager.gotoSendToLiveDBMS(databaseUUID);
    }
  }
}
