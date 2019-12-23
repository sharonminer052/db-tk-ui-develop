package com.databasepreservation.common.client.common.visualization.browse.configuration;

import java.util.HashMap;
import java.util.Map;

import com.databasepreservation.common.client.common.lists.widgets.MultipleSelectionTablePanel;
import com.databasepreservation.common.client.common.visualization.browse.configuration.handler.ConfigurationHandler;
import com.databasepreservation.common.client.models.configuration.denormalize.RelatedColumnConfiguration;
import com.databasepreservation.common.client.models.configuration.denormalize.RelatedTablesConfiguration;
import com.databasepreservation.common.client.models.structure.ViewerColumn;
import com.databasepreservation.common.client.models.structure.ViewerTable;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Label;

import config.i18n.client.ClientMessages;

/**
 * @author Gabriel Barros <gbarros@keep.pt>
 */
public class TransformationChildTables {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);
  private static Map<String, TransformationChildTables> instances = new HashMap<>();
  private ConfigurationHandler configuration;
  private TransformationTable rootTable;
  private ViewerTable childTable;
  private Map<Integer, Boolean> isLoading = new HashMap<>();
  private String uuid;

  /**
   *
   * @param childTable
   * @param configuration
   * @param rootTable
   * @return
   */
  public static TransformationChildTables getInstance(TableNode childTable, ConfigurationHandler configuration, TransformationTable rootTable) {
    return instances.computeIfAbsent(childTable.getUuid(),
      k -> new TransformationChildTables(childTable, configuration, rootTable));
  }

  /**
   * @param childTable
   * @param configuration
   * @param rootTable
   */
  private TransformationChildTables(TableNode childTable, ConfigurationHandler configuration, TransformationTable rootTable) {
    this.configuration = configuration;
    this.rootTable = rootTable;
    this.childTable = childTable.getTable();
    this.uuid = childTable.getUuid();
    preset();
  }

  /**
   *
   */
  private void preset() {
    for (ViewerColumn columns : childTable.getColumns()) {
      isLoading.put(columns.getColumnIndexInEnclosingTable(), true);
    }
  }

  /**
   *
   * @return
   */
  public MultipleSelectionTablePanel createTable() {
    MultipleSelectionTablePanel<ViewerColumn> selectionTablePanel = new MultipleSelectionTablePanel<>();
    Label header = new Label("");
    selectionTablePanel.createTable(header, childTable.getColumns().iterator(), createCheckbox(selectionTablePanel),
      new MultipleSelectionTablePanel.ColumnInfo<>(messages.columnName(), 7, new TextColumn<ViewerColumn>() {
        @Override
        public String getValue(ViewerColumn object) {
          return object.getDisplayName();
        }
      }), new MultipleSelectionTablePanel.ColumnInfo<>(messages.description(), 25, new TextColumn<ViewerColumn>() {
        @Override
        public String getValue(ViewerColumn object) {
          return object.getDescription();
        }
      }));

    return selectionTablePanel;
  }

  /**
   *
   * @param selectionTablePanel
   * @return
   */
  private MultipleSelectionTablePanel.ColumnInfo<ViewerColumn> createCheckbox(
    MultipleSelectionTablePanel<ViewerColumn> selectionTablePanel) {
    return new MultipleSelectionTablePanel.ColumnInfo<>("", 4,
      new Column<ViewerColumn, Boolean>(new CheckboxCell(true, true)) {

        @Override
        public Boolean getValue(ViewerColumn object) {
          int index = object.getColumnIndexInEnclosingTable();
          if (isLoading.get(index)) {
            isLoading.put(index, false);
            if (isSet(uuid, index)) {
              selectionTablePanel.getSelectionModel().setSelected(object, true);
            }
          } else {
            if (selectionTablePanel.getSelectionModel().isSelected(object)) {
              configuration.addColumnToInclude(uuid, object);
              rootTable.redrawTable();
            } else {
              configuration.removeColumnToInclude(uuid, object);
              rootTable.redrawTable();
            }
          }

          return selectionTablePanel.getSelectionModel().isSelected(object);
        }
      });
  }

  private boolean isSet(String uuid, int index) {
    RelatedTablesConfiguration relatedTable = configuration.getRelatedTable(uuid);
    if(relatedTable != null){
      for (RelatedColumnConfiguration column : relatedTable.getColumnsIncluded()) {
        if (column.getIndex() == index) {
          return true;
        }
      }
    }
    return false;
  }
}