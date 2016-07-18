package com.databasepreservation.visualization.client.common.sidebar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roda.core.data.v2.index.IsIndexed;

import com.databasepreservation.visualization.client.BrowserService;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerDatabase;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerMetadata;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerSchema;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerTable;
import com.databasepreservation.visualization.shared.client.Tools.FontAwesomeIconManager;
import com.databasepreservation.visualization.shared.client.Tools.HistoryManager;
import com.databasepreservation.visualization.shared.client.Tools.ViewerStringUtils;
import com.databasepreservation.visualization.shared.client.widgets.wcag.AccessibleFocusPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class DatabaseSidebar extends Composite {
  private static String SEARCH_PLACEHOLDER = "Filter this list";
  private static Map<String, DatabaseSidebar> instances = new HashMap<>();

  /**
   * Creates a new DatabaseSidebar, rarely hitting the database more than once
   * for each database.
   * 
   * @param databaseUUID
   *          the database UUID
   * @return a new DatabaseSidebar
   */
  public static DatabaseSidebar getInstance(String databaseUUID) {
    String code = databaseUUID;

    DatabaseSidebar instance = instances.get(code);
    if (instance == null || instance.database == null) {
      instance = new DatabaseSidebar(databaseUUID);
      instances.put(code, instance);
    } else {
      // workaround because the same DatabaseSidebar can not belong to multiple
      // widgets
      return new DatabaseSidebar(instance);
    }
    return instance;
  }

  interface DatabaseSidebarUiBinder extends UiBinder<Widget, DatabaseSidebar> {
  }

  private static DatabaseSidebarUiBinder uiBinder = GWT.create(DatabaseSidebarUiBinder.class);

  @UiField
  FlowPanel sidebarGroup;

  @UiField
  FlowPanel searchPanel;

  @UiField
  TextBox searchInputBox;

  @UiField
  AccessibleFocusPanel searchInputButton;

  private ViewerDatabase database;

  /**
   * Clone constructor, because the same DatabaseSidebar can not be child in
   * more than one widget
   * 
   * @param other
   *          the DatabaseSidebar used in another widget
   */
  private DatabaseSidebar(DatabaseSidebar other) {
    initWidget(uiBinder.createAndBindUi(this));
    database = other.database;
    init();
  }

  /**
   * Use DatabaseSidebar.getInstance to obtain an instance
   */
  private DatabaseSidebar(String databaseUUID) {
    initWidget(uiBinder.createAndBindUi(this));
    searchInit();

    BrowserService.Util.getInstance().retrieve(ViewerDatabase.class.getName(), databaseUUID,
      new AsyncCallback<IsIndexed>() {
        @Override
        public void onFailure(final Throwable caught) {
          throw new RuntimeException(caught);
        }

        @Override
        public void onSuccess(IsIndexed result) {
          database = (ViewerDatabase) result;
          init();
        }
      });
  }

  private void init() {
    // database metadata
    final ViewerMetadata metadata = database.getMetadata();

    sidebarGroup.add(new SidebarItem("Database").addIcon(FontAwesomeIconManager.DATABASE).setH5().setIndent0());

    sidebarGroup.add(new SidebarHyperlink("Information", HistoryManager.linkToDatabase(database.getUUID()))
      .addIcon(FontAwesomeIconManager.DATABASE_INFORMATION).setH6().setIndent1());

    sidebarGroup.add(new SidebarHyperlink("Users & Roles", HistoryManager.linkToDatabaseUsers(database.getUUID()))
      .addIcon(FontAwesomeIconManager.DATABASE_USERS).setH6().setIndent1());

    for (final ViewerSchema schema : metadata.getSchemas()) {
      sidebarGroup.add(new SidebarItem(schema.getName()).addIcon(FontAwesomeIconManager.SCHEMA).setH5().setIndent0());

      sidebarGroup.add(new SidebarHyperlink("Structure", HistoryManager.linkToSchemaStructure(database.getUUID(),
        schema.getUUID())).addIcon(FontAwesomeIconManager.SCHEMA_STRUCTURE).setH6().setIndent1());

      sidebarGroup.add(new SidebarHyperlink("Routines", HistoryManager.linkToSchemaRoutines(database.getUUID(),
        schema.getUUID())).addIcon(FontAwesomeIconManager.SCHEMA_ROUTINES).setH6().setIndent1());

      sidebarGroup.add(new SidebarHyperlink("Triggers", HistoryManager.linkToSchemaTriggers(database.getUUID(),
        schema.getUUID())).addIcon(FontAwesomeIconManager.SCHEMA_TRIGGERS).setH6().setIndent1());

      sidebarGroup.add(new SidebarHyperlink("Check constraints", HistoryManager.linkToSchemaCheckConstraints(
        database.getUUID(), schema.getUUID())).addIcon(FontAwesomeIconManager.SCHEMA_CHECK_CONSTRAINTS).setH6()
        .setIndent1());

      sidebarGroup.add(new SidebarHyperlink("Views", HistoryManager.linkToSchemaViews(database.getUUID(),
        schema.getUUID())).addIcon(FontAwesomeIconManager.SCHEMA_VIEWS).setH6().setIndent1());

      sidebarGroup.add(new SidebarItem("Data").addIcon(FontAwesomeIconManager.SCHEMA_DATA).setH6().setIndent1());

      for (ViewerTable table : schema.getTables()) {
        sidebarGroup.add(new SidebarHyperlink(table.getName(), HistoryManager.linkToTable(database.getUUID(),
          table.getUUID())).addIcon(FontAwesomeIconManager.TABLE).setH6().setIndent2());
      }

      searchInit();
    }
  }

  private void searchInit() {
    searchInputBox.getElement().setPropertyString("placeholder", SEARCH_PLACEHOLDER);

    searchInputBox.addChangeHandler(new ChangeHandler() {
      @Override
      public void onChange(ChangeEvent event) {
        doSearch();
      }
    });

    searchInputBox.addKeyDownHandler(new KeyDownHandler() {
      @Override
      public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          doSearch();
        }
      }
    });

    searchInputButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        doSearch();
      }
    });
  }

  private void doSearch() {
    String searchValue = searchInputBox.getValue();

    if (ViewerStringUtils.isBlank(searchValue)) {
      // show all
      for (Widget widget : sidebarGroup) {
        widget.setVisible(true);
      }
    } else {
      // show matching

      Set<SidebarItem> parents = new HashSet<>();
      List<SidebarItem> parentIndents = new ArrayList<>();


      for (Widget widget : sidebarGroup) {
        if (widget instanceof SidebarItem) {
          SidebarItem sidebarItem = (SidebarItem) widget;
          if (sidebarItem.getText().contains(searchValue)) {
            widget.setVisible(true);
          } else {
            widget.setVisible(false);
          }
        } else {
          widget.setVisible(true);
        }
      }
    }
  }
}
