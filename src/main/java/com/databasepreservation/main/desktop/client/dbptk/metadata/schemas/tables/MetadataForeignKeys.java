package com.databasepreservation.main.desktop.client.dbptk.metadata.schemas.tables;

import java.util.ArrayList;
import java.util.List;

import com.databasepreservation.main.common.shared.ViewerStructure.ViewerColumn;
import com.databasepreservation.main.common.shared.ViewerStructure.ViewerDatabase;
import com.databasepreservation.main.common.shared.ViewerStructure.ViewerForeignKey;
import com.databasepreservation.main.common.shared.ViewerStructure.ViewerReference;
import com.databasepreservation.main.common.shared.ViewerStructure.ViewerSIARDBundle;
import com.databasepreservation.main.common.shared.ViewerStructure.ViewerSchema;
import com.databasepreservation.main.common.shared.ViewerStructure.ViewerTable;
import com.databasepreservation.main.desktop.client.common.EditableCell;
import com.databasepreservation.main.desktop.client.common.lists.MetadataTableList;
import com.databasepreservation.main.desktop.client.dbptk.metadata.MetadataControlPanel;
import com.databasepreservation.main.desktop.client.dbptk.metadata.MetadataEditPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;

import config.i18n.client.ClientMessages;

/**
 * @author Gabriel Barros <gbarros@keep.pt>
 */
public class MetadataForeignKeys implements MetadataEditPanel {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);
  private final MetadataControlPanel controls;
  private ViewerSIARDBundle SIARDbundle;
  private ViewerDatabase database;
  private ViewerTable table;
  private ViewerSchema schema;
  private String type = "foreignKey";

  MetadataForeignKeys(ViewerSIARDBundle SIARDbundle, ViewerDatabase database, ViewerSchema schema, ViewerTable table, MetadataControlPanel controls) {
    this.SIARDbundle = SIARDbundle;
    this.database = database;
    this.table = table;
    this.schema = schema;
    this.controls = controls;
  }

  @Override
  public MetadataTableList createTable() {

    List<ViewerForeignKey> columns = table.getForeignKeys();

    if (columns.isEmpty()) {
      return new MetadataTableList<>(messages.tableDoesNotContainForeignKeys());
    } else {

      return new MetadataTableList<>(columns.iterator(), new MetadataTableList.ColumnInfo<>(
        messages.references_foreignKeyName(), 15, new TextColumn<ViewerForeignKey>() {
          @Override
          public String getValue(ViewerForeignKey object) {
            return object.getName();
          }
        }), new MetadataTableList.ColumnInfo<>(messages.foreignKeys_referencedTable(), 15,
          new TextColumn<ViewerForeignKey>() {
            @Override
            public String getValue(ViewerForeignKey object) {
              return database.getMetadata().getTable(object.getReferencedTableUUID()).getName();
            }
          }),
        new MetadataTableList.ColumnInfo<>(messages.columnName(), 15, new TextColumn<ViewerForeignKey>() {
          @Override
          public String getValue(ViewerForeignKey object) {
            return getReferenceList(object);
          }
        }),
        new MetadataTableList.ColumnInfo<>(messages.foreignKeys_deleteAction(), 15, new TextColumn<ViewerForeignKey>() {
          @Override
          public String getValue(ViewerForeignKey object) {
            return object.getDeleteAction();
          }
        }),
        new MetadataTableList.ColumnInfo<>(messages.foreignKeys_updateAction(), 15, new TextColumn<ViewerForeignKey>() {
          @Override
          public String getValue(ViewerForeignKey object) {
            return object.getUpdateAction();
          }
        }), new MetadataTableList.ColumnInfo<>(messages.description(), 15, getDescriptionColumn()));
    }
  }

  private String getReferenceList(ViewerForeignKey object) {
    List<ViewerColumn> tableColumns = database.getMetadata().getTable(object.getReferencedTableUUID()).getColumns();
    List<String> referanceColumns = new ArrayList<>();
    for (ViewerReference reference : object.getReferences()) {
      referanceColumns.add(tableColumns.get(reference.getReferencedColumnIndex()).getDisplayName());
    }
    return referanceColumns.toString();
  }

  @Override
  public Column<ViewerForeignKey, String> getDescriptionColumn() {
    Column<ViewerForeignKey, String> description = new Column<ViewerForeignKey, String>(new EditableCell()) {
      @Override
      public String getValue(ViewerForeignKey object) {
        return object.getDescription();
      }
    };

    description.setFieldUpdater((index, object, value) -> {
      object.setDescription(value);
      updateSIARDbundle(object.getName(), value);
    });

    return description;
  }

  @Override
  public void updateSIARDbundle(String name, String value) {
    SIARDbundle.setTableType(schema.getName(), table.getName(), type, name, value);
    controls.validate();
  }
}
