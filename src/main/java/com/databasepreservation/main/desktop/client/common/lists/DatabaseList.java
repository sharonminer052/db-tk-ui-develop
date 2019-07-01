package com.databasepreservation.main.desktop.client.common.lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roda.core.data.v2.index.IndexResult;
import org.roda.core.data.v2.index.facet.Facets;
import org.roda.core.data.v2.index.filter.Filter;
import org.roda.core.data.v2.index.sort.Sorter;
import org.roda.core.data.v2.index.sublist.Sublist;

import com.databasepreservation.main.common.client.BrowserService;
import com.databasepreservation.main.common.shared.ViewerConstants;
import com.databasepreservation.main.common.shared.ViewerStructure.ViewerDatabase;
import com.databasepreservation.main.common.shared.client.ClientLogger;
import com.databasepreservation.main.common.shared.client.common.utils.ApplicationType;
import com.databasepreservation.main.common.shared.client.common.utils.JavascriptUtils;
import com.databasepreservation.main.common.shared.client.tools.HistoryManager;
import com.databasepreservation.main.common.shared.client.tools.PathUtils;
import com.databasepreservation.main.common.shared.client.tools.SolrHumanizer;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

import config.i18n.client.ClientMessages;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class DatabaseList extends BasicAsyncTableCell<ViewerDatabase> {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);
  private final ClientLogger logger = new ClientLogger(getClass().getName());

  private static final String OPEN_VALIDATED_SPAN = "<span>&#10004;</span>";
  private static final String OPEN_NOT_VALIDATED_SPAN = "<span>&#10006;</span>";

  public DatabaseList() {
    this(null, null, null, false, false);
  }

  private DatabaseList(Filter filter, Facets facets, String summary, boolean selectable, boolean exportable) {
    super(filter, facets, summary, selectable, exportable, 15, 50);
    autoUpdate(2000);
  }

  @Override
  protected void configureDisplay(CellTable<ViewerDatabase> display) {
    Column<ViewerDatabase, SafeHtml> nameColumn = new TooltipDatabaseColumn() {
      @Override
      public SafeHtml getValue(ViewerDatabase database) {
        return database != null && database.getMetadata() != null
          ? SafeHtmlUtils.fromString(database.getMetadata().getName())
          : SafeHtmlUtils.fromString("unknown");
      }
    };

    Column<ViewerDatabase, SafeHtml> dbmsColumn = new TooltipDatabaseColumn() {
      @Override
      public SafeHtml getValue(ViewerDatabase database) {
        return database != null && database.getMetadata() != null
          ? SafeHtmlUtils.fromString(database.getMetadata().getDatabaseProduct())
          : SafeHtmlUtils.fromString("unknown");
      }
    };

    Column<ViewerDatabase, SafeHtml> archivalDateColumn = new TooltipDatabaseColumn() {
      @Override
      public SafeHtml getValue(ViewerDatabase database) {
        return database != null && database.getMetadata() != null
          ? SafeHtmlUtils.fromString(database.getMetadata().getArchivalDate().substring(0, 10))
          : null;
      }
    };

    Column<ViewerDatabase, String> locationColumn = new ButtonDatabaseColumn() {
      @Override
      public String getValue(ViewerDatabase database) {
        return database != null && database.getMetadata() != null ? PathUtils.getFileName(database.getSIARDPath())
          : null;
      }
    };
    locationColumn.setFieldUpdater((index, object, value) -> {
      if (ApplicationType.getType().equals(ViewerConstants.ELECTRON)) {
        JavascriptUtils.showItemInFolder(object.getSIARDPath());
      }
    });

    Column<ViewerDatabase, SafeHtml> validColumn = new Column<ViewerDatabase, SafeHtml>(new SafeHtmlCell()) {
      @Override
      public SafeHtml getValue(ViewerDatabase database) {
        return database != null && database.getMetadata() != null ? getBooleanValue(database.getValidated())
          : getBooleanValue(false);
      }
    };

    Column<ViewerDatabase, SafeHtml> statusColumn = new TooltipDatabaseColumn() {
      @Override
      public SafeHtml getValue(ViewerDatabase database) {
        return database != null ? SafeHtmlUtils.fromString(SolrHumanizer.humanize(database.getStatus())) : null;
      }
    };

    Column<ViewerDatabase, String> openColumn = new ButtonDatabaseColumn() {
      @Override
      public String getValue(ViewerDatabase object) {
        return messages.open();
      }
    };
    openColumn.setFieldUpdater((index, object, value) -> {
      HistoryManager.gotoSIARDInfo(object.getUUID());
    });

    // nameColumn.setSortable(true);
    // archivalDateColumn.setSortable(true);
    // dataOriginTimespan.setSortable(true);
    // description.setSortable(true);

    addColumn(nameColumn, messages.databaseName(), true, TextAlign.NONE, 20);
    addColumn(dbmsColumn, messages.siardMetadata_producerApplication(), true, TextAlign.NONE, 10);
    addColumn(archivalDateColumn, messages.siardMetadata_archivalDate(), true, TextAlign.NONE, 5);
    addColumn(locationColumn, messages.SIARDLocation(), true, TextAlign.NONE, 8);
    addColumn(validColumn, messages.SIARDValidated(), true, TextAlign.CENTER, 4);
    addColumn(statusColumn, messages.databaseStatus(), true, TextAlign.NONE, 8);
    addColumn(openColumn, messages.actions(), true, TextAlign.NONE , 5);

    Label emptyInfo = new Label(messages.noItemsToDisplay());
    display.setEmptyTableWidget(emptyInfo);

    // define default sorting
    // display.getColumnSortList().push(new ColumnSortInfo(datesColumn, false));
    //
    // datesColumn.setCellStyleNames("nowrap");
    //
    // addStyleName("my-collections-table");
    // emptyInfo.addStyleName("my-collections-empty-info");
  }

  private static SafeHtml getBooleanValue(boolean value) {
    return value ? SafeHtmlUtils.fromSafeConstant(OPEN_VALIDATED_SPAN)
      : SafeHtmlUtils.fromSafeConstant(OPEN_NOT_VALIDATED_SPAN);
  }

  @Override
  protected void getData(Sublist sublist, ColumnSortList columnSortList,
    AsyncCallback<IndexResult<ViewerDatabase>> callback) {
    Filter filter = getFilter();

    Map<Column<ViewerDatabase, ?>, List<String>> columnSortingKeyMap = new HashMap<>();
    // columnSortingKeyMap.put(nameColumn, Arrays.asList("id"));
    // columnSortingKeyMap.put(archivalDateColumn,
    // Arrays.asList("archivalList"));

    Sorter sorter = createSorter(columnSortList, columnSortingKeyMap);

    GWT.log("Filter: " + filter);

    BrowserService.Util.getInstance().findDatabases(filter, sorter, sublist, getFacets(),
      LocaleInfo.getCurrentLocale().getLocaleName(), callback);
  }

  @Override
  public void exportVisibleClickHandler() {

  }

  @Override
  public void exportAllClickHandler() {

  }

  private abstract static class TooltipDatabaseColumn extends Column<ViewerDatabase, SafeHtml> {
    public TooltipDatabaseColumn() {
      super(new SafeHtmlCell());
    }

    @Override
    public void render(Cell.Context context, ViewerDatabase object, SafeHtmlBuilder sb) {
      SafeHtml value = getValue(object);
      if (value != null) {
        sb.appendHtmlConstant("<div title=\"" + SafeHtmlUtils.htmlEscape(value.asString()) + "\">");
        sb.append(value);
        sb.appendHtmlConstant("</div");
      }
    }
  }

  private abstract static class ButtonDatabaseColumn extends Column<ViewerDatabase, String> {
    public ButtonDatabaseColumn() {
      super(new ButtonCell());
    }

    @Override
    public void render(Cell.Context context, ViewerDatabase object, SafeHtmlBuilder sb) {
      String value = getValue(object);
      sb.appendHtmlConstant("<button class=\"btn btn-link-info\" type=\"button\" tabindex=\"-1\">");
      if (value != null) {
        sb.append(SafeHtmlUtils.fromString(value));
      }
      sb.appendHtmlConstant("</button>");
    }
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    refresh();
  }
}
