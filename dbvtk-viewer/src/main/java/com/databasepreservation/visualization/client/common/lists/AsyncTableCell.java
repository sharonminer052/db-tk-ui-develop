/**
 * The contents of this file are based on those found at https://github.com/keeps/roda
 * and are subject to the license and copyright detailed in https://github.com/keeps/roda
 */
package com.databasepreservation.visualization.client.common.lists;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.roda.core.data.adapter.facet.Facets;
import org.roda.core.data.adapter.filter.Filter;
import org.roda.core.data.adapter.sort.SortParameter;
import org.roda.core.data.adapter.sort.Sorter;
import org.roda.core.data.adapter.sublist.Sublist;
import org.roda.core.data.v2.index.IndexResult;
import org.roda.core.data.v2.index.IsIndexed;
import org.roda.core.data.v2.index.SelectedItems;
import org.roda.core.data.v2.index.SelectedItemsFilter;
import org.roda.core.data.v2.index.SelectedItemsList;

import com.databasepreservation.visualization.shared.client.ClientLogger;
import com.databasepreservation.visualization.shared.client.widgets.MyCellTableResources;
import com.databasepreservation.visualization.shared.client.widgets.wcag.AccessibleCellTable;
import com.databasepreservation.visualization.shared.client.widgets.wcag.AccessibleSimplePager;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.PageSizePager;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.CellPreviewEvent.Handler;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SingleSelectionModel;

// import config.i18n.client.BrowseMessages;

public abstract class AsyncTableCell<T extends IsIndexed, O> extends FlowPanel implements
  HasValueChangeHandlers<IndexResult<T>> {

  // private static final BrowseMessages messages =
  // GWT.create(BrowseMessages.class);

  private final MyAsyncDataProvider<T> dataProvider;
  private final SingleSelectionModel<T> selectionModel;
  private final AsyncHandler columnSortHandler;

  private final AccessibleSimplePager resultsPager;
  private final PageSizePager pageSizePager;
  private final CellTable<T> display;
  private final Button exportButton;

  private FlowPanel selectAllPanel;
  private FlowPanel selectAllPanelBody;
  private Label selectAllLabel;
  private CheckBox selectAllCheckBox;

  private Column<T, Boolean> selectColumn;
  private Set<T> selected = new HashSet<T>();
  private final List<CheckboxSelectionListener<T>> listeners = new ArrayList<AsyncTableCell.CheckboxSelectionListener<T>>();

  private Filter filter;
  private boolean justActive;
  private Facets facets;
  private boolean selectable;

  private final ClientLogger logger = new ClientLogger(getClass().getName());

  private int initialPageSize = 20;
  private int pageSizeIncrement = 100;

  private Class<T> selectedClass;
  private final O object;

  public AsyncTableCell() {
    this(null, false, null, null, false, false, 20, 100, null);
  }

  public AsyncTableCell(Filter filter, boolean justActive, Facets facets, String summary, boolean selectable,
    boolean exportable, O object) {
    this(filter, justActive, facets, summary, selectable, exportable, 20, 100, object);
  }

  public AsyncTableCell(Filter filter, boolean justActive, Facets facets, String summary, boolean selectable,
    boolean exportable, int initialPageSize, int pageSizeIncrement, O object) {
    super();

    this.initialPageSize = initialPageSize;
    this.pageSizeIncrement = pageSizeIncrement;
    this.object = object;

    if (summary == null) {
      summary = "summary" + Random.nextInt(1000);
    }

    this.filter = filter;
    this.justActive = justActive;
    this.facets = facets;
    this.selectable = selectable;

    display = new AccessibleCellTable<T>(getInitialPageSize(),
      (MyCellTableResources) GWT.create(MyCellTableResources.class), getKeyProvider(), summary);
    display.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
    display
      .setLoadingIndicator(new HTML(
        SafeHtmlUtils
          .fromSafeConstant("<div class='spinner'><div class='double-bounce1'></div><div class='double-bounce2'></div></div>")));

    configure(display);

    this.dataProvider = new MyAsyncDataProvider<T>(display, new IndexResultDataProvider<T>() {

      @Override
      public void getData(Sublist sublist, ColumnSortList columnSortList, AsyncCallback<IndexResult<T>> callback) {
        AsyncTableCell.this.getData(sublist, columnSortList, callback);
      }
    }) {

      @Override
      protected void fireChangeEvent(IndexResult<T> result) {
        ValueChangeEvent.fire(AsyncTableCell.this, result);
      }
    };

    dataProvider.addDataDisplay(display);

    if (exportable) {
      exportButton = new Button("Export visible", new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
          AsyncTableCell.this.exportClickHandler();
        }
      });
    } else {
      exportButton = null;
    }

    resultsPager = new AccessibleSimplePager(AccessibleSimplePager.TextLocation.LEFT,
      (SimplePager.Resources) GWT.create(SimplePager.Resources.class), false, initialPageSize, false, false,
      (SimplePager.ImageButtonsConstants) GWT.create(SimplePager.ImageButtonsConstants.class));
    resultsPager.setDisplay(display);

    pageSizePager = new PageSizePager(getPageSizePagerIncrement());
    pageSizePager.setDisplay(display);

    createSelectAllPanel();

    add(selectAllPanel);
    add(new ScrollPanel(display));
    add(resultsPager);
    if (exportButton != null) {
      add(exportButton);
    }
    add(pageSizePager);

    selectionModel = new SingleSelectionModel<>(getKeyProvider());

    Handler<T> selectionEventManager = getSelectionEventManager();
    if (selectionEventManager != null) {
      display.setSelectionModel(selectionModel, selectionEventManager);
    } else {
      display.setSelectionModel(selectionModel);
    }

    columnSortHandler = new AsyncHandler(display);
    display.addColumnSortHandler(columnSortHandler);

    addStyleName("my-asyncdatagrid");
    resultsPager.addStyleName("my-asyncdatagrid-pager-results");
    pageSizePager.addStyleName("my-asyncdatagrid-pager-pagesize");
    display.addStyleName("my-asyncdatagrid-display");
    if (exportButton != null) {
      exportButton.addStyleName("btn btn-export");
    }

    addValueChangeHandler(new ValueChangeHandler<IndexResult<T>>() {
      @Override
      public void onValueChange(ValueChangeEvent<IndexResult<T>> event) {
        selected = new HashSet<T>();
        hideSelectAllPanel();
      }
    });

    Label emptyInfo = new Label("No items to display");
    display.setEmptyTableWidget(emptyInfo);
  }

  private void configure(final CellTable<T> display) {
    if (selectable) {
      selectColumn = new Column<T, Boolean>(new CheckboxCell(true, false)) {
        @Override
        public Boolean getValue(T object) {
          return selected.contains(object);
        }
      };

      selectColumn.setFieldUpdater(new FieldUpdater<T, Boolean>() {
        @Override
        public void update(int index, T object, Boolean isSelected) {
          if (isSelected) {
            selected.add(object);
          } else {
            selected.remove(object);
          }

          // update header
          display.redrawHeaders();
          fireOnCheckboxSelectionChanged();
        }
      });

      Header<Boolean> selectHeader = new Header<Boolean>(new CheckboxCell(true, true)) {

        @Override
        public Boolean getValue() {
          Boolean ret;

          if (selected.isEmpty()) {
            ret = false;
          } else if (selected.containsAll(getVisibleItems())) {
            ret = true;
            showSelectAllPanel();
          } else {
            // some are selected
            ret = false;
            hideSelectAllPanel();
          }

          return ret;
        }
      };

      selectHeader.setUpdater(new ValueUpdater<Boolean>() {

        @Override
        public void update(Boolean value) {
          if (value) {
            selected.addAll(getVisibleItems());
            showSelectAllPanel();
          } else {
            selected.clear();
            hideSelectAllPanel();
          }
          redraw();
          fireOnCheckboxSelectionChanged();
        }
      });

      display.addColumn(selectColumn, selectHeader);
      display.setColumnWidth(selectColumn, "45px");
    }
    configureDisplay(display);
  }

  protected abstract void configureDisplay(CellTable<T> display);

  protected int getInitialPageSize() {
    return initialPageSize;
  }

  protected ProvidesKey<T> getKeyProvider() {
    return new ProvidesKey<T>() {

      @Override
      public Object getKey(T item) {
        return item.getUUID();
      }
    };
  }

  protected abstract void getData(Sublist sublist, ColumnSortList columnSortList, AsyncCallback<IndexResult<T>> callback);

  protected int getPageSizePagerIncrement() {
    return pageSizeIncrement;
  }

  protected CellPreviewEvent.Handler<T> getSelectionEventManager() {
    if (selectable) {
      return DefaultSelectionEventManager.<T> createBlacklistManager(0);
    } else {
      return null;
    }
  }

  public SingleSelectionModel<T> getSelectionModel() {
    return selectionModel;
  }

  public void refresh() {
    selected = new HashSet<T>();
    hideSelectAllPanel();
    display.setVisibleRangeAndClearData(new Range(0, getInitialPageSize()), true);
    getSelectionModel().clear();
  }

  public void update() {
    dataProvider.update();
  }

  private Timer autoUpdateTimer = null;
  private int autoUpdateTimerMillis = 0;

  public void autoUpdate(int periodMillis) {
    if (autoUpdateTimer != null) {
      autoUpdateTimer.cancel();
    }

    autoUpdateTimer = new Timer() {

      @Override
      public void run() {
        dataProvider.update(new AsyncCallback<Void>() {

          @Override
          public void onFailure(Throwable caught) {
            // disable auto-update
            autoUpdateTimer.cancel();
          }

          @Override
          public void onSuccess(Void result) {
            // do nothing
          }
        });
      }
    };

    autoUpdateTimerMillis = periodMillis;
    if (this.isAttached()) {
      autoUpdateTimer.scheduleRepeating(periodMillis);
    }

  }

  @Override
  protected void onDetach() {
    if (autoUpdateTimer != null) {
      autoUpdateTimer.cancel();
    }
    super.onDetach();
  }

  @Override
  protected void onLoad() {
    if (autoUpdateTimer != null && autoUpdateTimerMillis > 0 && !autoUpdateTimer.isRunning()) {
      autoUpdateTimer.scheduleRepeating(autoUpdateTimerMillis);
    }
    super.onLoad();
  }

  public void redraw() {
    display.redraw();
  }

  public Filter getFilter() {
    return filter;
  }

  public boolean getJustActive() {
    return justActive;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
    refresh();
  }

  @Override
  public HandlerRegistration addValueChangeHandler(ValueChangeHandler<IndexResult<T>> handler) {
    return addHandler(handler, ValueChangeEvent.getType());
  }

  public Facets getFacets() {
    return facets;
  }

  public void setFacets(Facets facets) {
    this.facets = facets;
    refresh();
  }

  public List<T> getVisibleItems() {
    return display.getVisibleItems();
  }

  protected Sorter createSorter(ColumnSortList columnSortList, Map<Column<T, ?>, List<String>> columnSortingKeyMap) {
    Sorter sorter = new Sorter();
    for (int i = 0; i < columnSortList.size(); i++) {
      ColumnSortInfo columnSortInfo = columnSortList.get(i);

      List<String> sortParameterKeys = columnSortingKeyMap.get(columnSortInfo.getColumn());

      if (sortParameterKeys != null) {
        for (String sortParameterKey : sortParameterKeys) {
          sorter.add(new SortParameter(sortParameterKey, !columnSortInfo.isAscending()));
        }
      } else {
        logger.warn("Selecting a sorter that is not mapped");
      }
    }
    return sorter;
  }

  public void nextItemSelection() {
    nextItemSelection(false);
  }

  public void nextItemSelection(boolean nextPageJump) {
    if (getSelectionModel().getSelectedObject() != null) {
      T selectedItem = getSelectionModel().getSelectedObject();
      int selectedIndex = getVisibleItems().indexOf(selectedItem);

      if (nextPageJump) {
        if (selectedIndex == -1) {
          getSelectionModel().setSelected(getVisibleItems().get(0), true);
        } else {
          getSelectionModel().setSelected(getVisibleItems().get(selectedIndex + 1), true);
        }
      } else {
        if (selectedIndex < getVisibleItems().size() - 1) {
          getSelectionModel().setSelected(getVisibleItems().get(selectedIndex + 1), true);
        }
      }
    } else {
      getSelectionModel().setSelected(getVisibleItems().get(0), true);
    }
  }

  public void previousItemSelection() {
    previousItemSelection(false);
  }

  public void previousItemSelection(boolean previousPageJump) {
    if (getSelectionModel().getSelectedObject() != null) {
      T selectedItem = getSelectionModel().getSelectedObject();
      int selectedIndex = getVisibleItems().indexOf(selectedItem);

      if (previousPageJump) {
        if (selectedIndex == -1) {
          getSelectionModel().setSelected(getVisibleItems().get(getVisibleItems().size() - 1), true);
        } else {
          getSelectionModel().setSelected(getVisibleItems().get(selectedIndex - 1), true);
        }
      } else {
        if (selectedIndex > 0) {
          getSelectionModel().setSelected(getVisibleItems().get(selectedIndex - 1), true);
        }
      }
    } else {
      getSelectionModel().setSelected(getVisibleItems().get(0), true);
    }
  }

  public boolean nextPageOnNextFile() {
    boolean nextPage = false;
    if (getSelectionModel().getSelectedObject() != null) {
      T selectedItem = getSelectionModel().getSelectedObject();
      if (getVisibleItems().indexOf(selectedItem) == (resultsPager.getPageSize() - 1) && resultsPager.hasNextPage()) {
        nextPage = true;
      }
    }
    return nextPage;
  }

  public boolean previousPageOnPreviousFile() {
    boolean previousPage = false;
    if (getSelectionModel().getSelectedObject() != null) {
      T selectedItem = getSelectionModel().getSelectedObject();
      if (getVisibleItems().indexOf(selectedItem) == 0 && resultsPager.hasPreviousPage()) {
        previousPage = true;
      }
    }
    return previousPage;
  }

  public void nextPage() {
    resultsPager.nextPage();
  }

  public void prevousPage() {
    resultsPager.previousPage();
  }

  public boolean isSelectable() {
    return selectable;
  }

  public void setSelectable(boolean selectable) {
    this.selectable = selectable;
  }

  public SelectedItems<T> getSelected() {
    SelectedItems<T> ret;
    if (isAllSelected()) {
      ret = new SelectedItemsFilter<T>(getFilter(), selectedClass.getName(), getJustActive());
    } else {
      List<String> ids = new ArrayList<>();

      for (T item : selected) {
        ids.add(item.getUUID());
      }

      ret = new SelectedItemsList<T>(ids, selectedClass.getName());
    }

    return ret;
  }

  public void setSelected(Set<T> newSelected) {
    selected.clear();
    selected.addAll(newSelected);
    redraw();
    fireOnCheckboxSelectionChanged();
  }

  public void clearSelected() {
    selected.clear();
    redraw();
    fireOnCheckboxSelectionChanged();
  }

  // LISTENER

  public interface CheckboxSelectionListener<T extends IsIndexed> {
    public void onSelectionChange(SelectedItems<T> selected);
  }

  public void addCheckboxSelectionListener(CheckboxSelectionListener<T> checkboxSelectionListener) {
    listeners.add(checkboxSelectionListener);
  }

  public void removeCheckboxSelectionListener(CheckboxSelectionListener<T> listener) {
    listeners.remove(listener);
  }

  public void fireOnCheckboxSelectionChanged() {
    for (CheckboxSelectionListener<T> listener : listeners) {
      listener.onSelectionChange(getSelected());
    }
  }

  // SELECT ALL PANEL

  public void createSelectAllPanel() {
    selectAllPanel = new FlowPanel();
    selectAllPanelBody = new FlowPanel();
    selectAllCheckBox = new CheckBox();
    selectAllLabel = new Label("Select all");

    selectAllPanelBody.add(selectAllCheckBox);
    selectAllPanelBody.add(selectAllLabel);
    selectAllPanel.add(selectAllPanelBody);
    selectAllPanel.setVisible(false);

    selectAllPanel.addStyleName("panel");
    selectAllPanelBody.addStyleName("panel-body");

    selectAllCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event) {
        fireOnCheckboxSelectionChanged();
      }
    });

  }

  public void showSelectAllPanel() {
    if (!selectAllPanel.isVisible() && resultsPager.hasNextPage() || resultsPager.hasPreviousPage()) {
      // selectAllLabel.setText(messages.listSelectAllMessage(dataProvider.getRowCount()));
      selectAllLabel.setText("Select all " + dataProvider.getRowCount() + " items");
      selectAllCheckBox.setValue(false);
      selectAllPanel.setVisible(true);
    }
  }

  public void hideSelectAllPanel() {
    selectAllCheckBox.setValue(false);
    selectAllPanel.setVisible(false);
  }

  public Boolean isAllSelected() {
    return selectAllCheckBox.getValue();
  }

  public O getObject() {
    return object;
  }

  public int getRowCount() {
    return dataProvider.getRowCount();
  }

  public Date getDate() {
    return dataProvider.getDate();
  }

  public List<CheckboxSelectionListener<T>> getListeners() {
    return this.listeners;
  }

  public Class<T> getSelectedClass() {
    return this.selectedClass;
  }

  public void setSelectedClass(Class<T> selectedClass) {
    this.selectedClass = selectedClass;
  }

  protected void addColumn(Column<T, ?> column, SafeHtml headerHTML, boolean nowrap, boolean alignRight) {
    SafeHtmlHeader header = new SafeHtmlHeader(headerHTML);

    display.addColumn(column, header);

    if (nowrap && alignRight) {
      header.setHeaderStyleNames("nowrap text-align-right");
      column.setCellStyleNames("nowrap text-align-right");
    } else if (nowrap) {
      header.setHeaderStyleNames("cellTableFadeOut");
      column.setCellStyleNames("cellTableFadeOut");
    }
  }

  protected void addColumn(Column<T, ?> column, SafeHtml headerHTML, boolean nowrap, boolean alignRight,
    double fixedSize) {
    addColumn(column, headerHTML, nowrap, alignRight);
    display.setColumnWidth(column, fixedSize, Style.Unit.EM);
  }

  protected void addColumn(Column<T, ?> column, String headerText, boolean nowrap, boolean alignRight) {
    addColumn(column, SafeHtmlUtils.fromString(headerText), nowrap, alignRight);
  }

  protected void addColumn(Column<T, ?> column, String headerText, boolean nowrap, boolean alignRight, double fixedSize) {
    addColumn(column, SafeHtmlUtils.fromString(headerText), nowrap, alignRight, fixedSize);
  }

  public abstract String getExportURL();

  public abstract void exportClickHandler();
}
