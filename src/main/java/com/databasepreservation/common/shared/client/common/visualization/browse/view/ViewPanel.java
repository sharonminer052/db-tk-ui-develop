package com.databasepreservation.common.shared.client.common.visualization.browse.view;

import java.util.HashMap;
import java.util.Map;

import com.databasepreservation.common.shared.ViewerStructure.ViewerDatabase;
import com.databasepreservation.common.shared.ViewerStructure.ViewerView;
import com.databasepreservation.common.shared.client.breadcrumb.BreadcrumbPanel;
import com.databasepreservation.common.shared.client.common.RightPanel;
import com.databasepreservation.common.shared.client.common.fields.MetadataField;
import com.databasepreservation.common.shared.client.common.utils.CommonClientUtils;
import com.databasepreservation.common.shared.client.tools.BreadcrumbManager;
import com.databasepreservation.common.shared.client.tools.FontAwesomeIconManager;
import com.databasepreservation.common.shared.client.tools.HistoryManager;
import com.databasepreservation.common.shared.client.tools.ViewerStringUtils;
import com.databasepreservation.common.shared.client.widgets.Alert;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import config.i18n.client.ClientMessages;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class ViewPanel extends RightPanel {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);
  private static Map<String, ViewPanel> instances = new HashMap<>();

  @Override
  public void handleBreadcrumb(BreadcrumbPanel breadcrumb) {
      BreadcrumbManager.updateBreadcrumb(breadcrumb, BreadcrumbManager.forView(database.getMetadata().getName(),
        database.getUUID(), view.getName(), view.getUUID()));
  }

  interface ViewPanelUiBinder extends UiBinder<Widget, ViewPanel> {
  }

  private static ViewPanelUiBinder uiBinder = GWT.create(ViewPanelUiBinder.class);
  private ViewerDatabase database;
  private ViewerView view;

  @UiField
  SimplePanel mainHeader;

  @UiField
  FlowPanel content;

  @UiField
  FlowPanel description;

  @UiField
  Button options;

  public static ViewPanel getInstance(ViewerDatabase database, String viewUUID) {
    String separator = "/";
    String code = database.getUUID() + separator + viewUUID;

    return instances.computeIfAbsent(code, k -> new ViewPanel(database, viewUUID));
  }

  private ViewPanel(ViewerDatabase database, String viewUUID) {
    this.database = database;
    this.view = database.getMetadata().getView(viewUUID);

    initWidget(uiBinder.createAndBindUi(this));

    init();
  }

  private void init() {
    mainHeader.setWidget(CommonClientUtils.getHeader(FontAwesomeIconManager.getTag(FontAwesomeIconManager.SCHEMA_VIEWS),
      view.getName(), "h1"));

    options.setText(messages.schemaStructurePanelTextForAdvancedOption());

    options.addClickHandler(event -> HistoryManager.gotoViewOptions(database.getUUID(), view.getUUID()));

    if (ViewerStringUtils.isNotBlank(view.getDescription())) {
      MetadataField instance = MetadataField.createInstance(view.getDescription());
      instance.setCSS("table-row-description");
      description.add(instance);
    }

    content.add(new Alert(Alert.MessageAlertType.WARNING, messages.viewPanelViewerNotMaterialized()));
  }
}