package com.databasepreservation.main.desktop.client.dbptk.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.databasepreservation.main.common.client.BrowserService;
import com.databasepreservation.main.common.shared.ViewerStructure.IsIndexed;
import com.databasepreservation.main.common.shared.ViewerStructure.ViewerDatabase;
import com.databasepreservation.main.common.shared.ViewerStructure.ViewerSIARDBundle;
import com.databasepreservation.main.common.shared.client.breadcrumb.BreadcrumbItem;
import com.databasepreservation.main.common.shared.client.breadcrumb.BreadcrumbPanel;
import com.databasepreservation.main.common.shared.client.common.DefaultAsyncCallback;
import com.databasepreservation.main.common.shared.client.tools.BreadcrumbManager;
import com.databasepreservation.main.desktop.client.common.sidebar.MetadataEditSidebar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import config.i18n.client.ClientMessages;

/**
 * @autor Gabriel Barros <gbarros@keep.pt>
 */
public class SIARDEditMetadataPage extends Composite {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);

  interface SIARDEditMetadataPageUiBinder extends UiBinder<Widget, SIARDEditMetadataPage> {
  }

  private static SIARDEditMetadataPageUiBinder binder = GWT.create(SIARDEditMetadataPageUiBinder.class);
  private static Map<String, SIARDEditMetadataPage> instances = new HashMap<>();
  private String databaseUUID;
  private ViewerDatabase database = null;
  private ViewerSIARDBundle SIARDbundle = new ViewerSIARDBundle();

  public static SIARDEditMetadataPage getInstance(String databaseUUID) {

    SIARDEditMetadataPage instance = instances.get(databaseUUID);
    if (instance == null) {
      instance = new SIARDEditMetadataPage(databaseUUID);
      instances.put(databaseUUID, instance);
    }
    return instance;
  }

  @UiField
  BreadcrumbPanel breadcrumb;

  @UiField(provided = true)
  MetadataEditSidebar sidebar;

  @UiField(provided = true)
  MetadataControlPanel controls;

  @UiField
  SimplePanel rightPanelContainer;

  private SIARDEditMetadataPage(final String databaseUUID) {
    this.databaseUUID = databaseUUID;
    this.sidebar = MetadataEditSidebar.getInstance(databaseUUID);
    this.controls = MetadataControlPanel.getInstance(databaseUUID);

    initWidget(binder.createAndBindUi(this));
  }

  public void load(MetadataPanelLoad rightPanelLoader, String sidebarSelected) {
    GWT.log("load. uuid: " + databaseUUID + ", database: " + database);

    if (databaseUUID != null
      && (database == null || !ViewerDatabase.Status.METADATA_ONLY.equals(database.getStatus()))) {
      loadPanelWithDatabase(rightPanelLoader, sidebarSelected);
    } else {
      loadPanel(rightPanelLoader, sidebarSelected);
    }
  }

  private void loadPanelWithDatabase(MetadataPanelLoad rightPanelLoader, String sidebarSelected) {
    BrowserService.Util.getInstance().retrieve(databaseUUID, ViewerDatabase.class.getName(), databaseUUID,
      new DefaultAsyncCallback<IsIndexed>() {
        @Override
        public void onFailure(Throwable caught) {
        }

        @Override
        public void onSuccess(IsIndexed result) {
          database = (ViewerDatabase) result;
          List<BreadcrumbItem> breadcrumbItems = BreadcrumbManager.forSIARDEditMetadataPage(databaseUUID, database.getMetadata().getName());
          BreadcrumbManager.updateBreadcrumb(breadcrumb, breadcrumbItems);
          loadPanel(rightPanelLoader, sidebarSelected);
        }
      });
  }

  private void loadPanel(MetadataPanelLoad rightPanelLoader, String sidebarSelected) {
    GWT.log("have db: " + database + "sb.init: " + sidebar.isInitialized());

    if (database != null && !sidebar.isInitialized()) {
      sidebar.init(database);
      controls.init(database, SIARDbundle);
    }

    sidebar.select(sidebarSelected);

    MetadataPanel rightPanel = rightPanelLoader.load(database, SIARDbundle);
    if (rightPanel != null) {
      rightPanel.handleBreadcrumb(breadcrumb);
      rightPanelContainer.setWidget(rightPanel);
      rightPanel.setVisible(true);
    }

  }
}
