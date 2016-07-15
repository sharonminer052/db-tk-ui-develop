package com.databasepreservation.visualization.client.browse;

import java.util.HashMap;
import java.util.Map;

import org.roda.core.data.adapter.filter.Filter;
import org.roda.core.data.v2.index.IsIndexed;

import com.databasepreservation.visualization.client.BrowserService;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerDatabase;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerSchema;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerTable;
import com.databasepreservation.visualization.client.common.search.SearchInfo;
import com.databasepreservation.visualization.client.common.search.SearchPanel;
import com.databasepreservation.visualization.client.common.search.TableSearchPanel;
import com.databasepreservation.visualization.client.common.sidebar.DatabaseSidebar;
import com.databasepreservation.visualization.client.common.utils.CommonClientUtils;
import com.databasepreservation.visualization.client.main.BreadcrumbPanel;
import com.databasepreservation.visualization.shared.client.Tools.BreadcrumbManager;
import com.databasepreservation.visualization.shared.client.Tools.FontAwesomeIconManager;
import com.databasepreservation.visualization.shared.client.Tools.ViewerStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class TablePanel extends Composite {
  private static Map<String, TablePanel> instances = new HashMap<>();

  public static TablePanel getInstance(String databaseUUID, String tableUUID) {
    return getInstance(databaseUUID, tableUUID, null);
  }

  public static TablePanel getInstance(String databaseUUID, String tableUUID, String searchInfoJson) {
    String separator = "/";
    String code = databaseUUID + separator + tableUUID;

    TablePanel instance = instances.get(code);
    if (instance == null) {
      instance = new TablePanel(databaseUUID, tableUUID, searchInfoJson);
      instances.put(code, instance);
    } else if (searchInfoJson != null) {
      instance.applySearchInfoJson(searchInfoJson);
    } else if (instance.tableSearchPanel.isSearchInfoDefined()) {
      instance = new TablePanel(databaseUUID, tableUUID);
      instances.put(code, instance);
    }

    return instance;
  }

  public static TablePanel createInstance(ViewerDatabase database, ViewerTable table, SearchInfo searchInfo) {
    return new TablePanel(database, table, searchInfo);
  }

  interface TablePanelUiBinder extends UiBinder<Widget, TablePanel> {
  }

  @UiField
  BreadcrumbPanel breadcrumb;

  @UiField(provided = true)
  SearchPanel dbSearchPanel;

  @UiField(provided = true)
  DatabaseSidebar sidebar;

  @UiField
  HTML mainHeader;

  @UiField(provided = true)
  TableSearchPanel tableSearchPanel;

  @UiField
  HTML description;

  private ViewerDatabase database;
  private ViewerSchema schema;
  private ViewerTable table;

  private static TablePanelUiBinder uiBinder = GWT.create(TablePanelUiBinder.class);

  /**
   * Synchronous Table panel that receives the data and does not need to
   * asynchronously query solr
   * 
   * @param database
   *          the database
   * @param table
   *          the table
   * @param searchInfo
   *          the predefined search
   */
  private TablePanel(ViewerDatabase database, ViewerTable table, SearchInfo searchInfo) {
    dbSearchPanel = new SearchPanel(new Filter(), "", "Search in all tables", false, false);
    sidebar = DatabaseSidebar.getInstance(database.getUUID());

    tableSearchPanel = new TableSearchPanel(searchInfo);

    initWidget(uiBinder.createAndBindUi(this));

    mainHeader.setHTML(FontAwesomeIconManager.loading(FontAwesomeIconManager.TABLE));

    BreadcrumbManager.updateBreadcrumb(breadcrumb, BreadcrumbManager.loadingTable(database.getUUID(), table.getUUID()));

    this.database = database;
    this.table = table;
    this.schema = database.getMetadata().getSchemaFromTableUUID(table.getUUID());
    init();
  }

  /**
   * Asynchronous table panel that receives UUIDs and needs to get the objects
   * from solr
   * 
   * @param databaseUUID
   *          the database UUID
   * @param tableUUID
   *          the table UUID
   */
  private TablePanel(final String databaseUUID, final String tableUUID) {
    this(databaseUUID, tableUUID, null);
  }

  /**
   * Asynchronous table panel that receives UUIDs and needs to get the objects
   * from solr. This method supports a predefined search (SearchInfo instance)
   * as a JSON String.
   *
   * @param databaseUUID
   *          the database UUID
   * @param tableUUID
   *          the table UUID
   * @param searchInfoJson
   *          the SearchInfo instance as a JSON String
   */
  private TablePanel(final String databaseUUID, final String tableUUID, String searchInfoJson) {
    dbSearchPanel = new SearchPanel(new Filter(), "", "Search in all tables", false, false);
    sidebar = DatabaseSidebar.getInstance(databaseUUID);
    if (searchInfoJson != null) {
      tableSearchPanel = new TableSearchPanel(searchInfoJson);
    } else {
      tableSearchPanel = new TableSearchPanel();
    }

    initWidget(uiBinder.createAndBindUi(this));

    mainHeader.setHTML(FontAwesomeIconManager.loading(FontAwesomeIconManager.TABLE));

    BreadcrumbManager.updateBreadcrumb(breadcrumb, BreadcrumbManager.loadingTable(databaseUUID, tableUUID));

    BrowserService.Util.getInstance().retrieve(ViewerDatabase.class.getName(), databaseUUID,
      new AsyncCallback<IsIndexed>() {
        @Override
        public void onFailure(Throwable caught) {
          throw new RuntimeException(caught);
        }

        @Override
        public void onSuccess(IsIndexed result) {
          database = (ViewerDatabase) result;
          table = database.getMetadata().getTable(tableUUID);
          schema = database.getMetadata().getSchemaFromTableUUID(tableUUID);
          init();
        }
      });
  }

  private void init() {
    mainHeader.setHTML(FontAwesomeIconManager.loaded(FontAwesomeIconManager.TABLE, table.getName()));

    BreadcrumbManager.updateBreadcrumb(
      breadcrumb,
      BreadcrumbManager.forTable(database.getMetadata().getName(), database.getUUID(), schema.getName(),
        schema.getUUID(), table.getName(), table.getUUID()));

    if (ViewerStringUtils.isNotBlank(table.getDescription())) {
      description.setHTML(CommonClientUtils.getFieldHTML("Description", table.getDescription()));
    }

    tableSearchPanel.provideSource(database, table);
  }

  private void applySearchInfoJson(String searchInfoJson) {
    tableSearchPanel.applySearchInfoJson(searchInfoJson);
  }
}
