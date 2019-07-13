package com.databasepreservation.main.desktop.client.dbptk.metadata.schemas.tables;

import com.databasepreservation.main.common.shared.ViewerStructure.*;
import com.databasepreservation.main.desktop.client.common.lists.MetadataTableList;
import com.databasepreservation.main.desktop.client.dbptk.metadata.schemas.MetadataTabPanel;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import config.i18n.client.ClientMessages;

import java.util.List;

/**
 * @author Gabriel Barros <gbarros@keep.pt>
 */
public class MetadataConstraints implements MetadataTabPanel {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);
  private final ViewerSIARDBundle SIARDbundle;
  private final ViewerDatabase database;

  MetadataConstraints(ViewerSIARDBundle SIARDbundle, ViewerDatabase database) {
    this.SIARDbundle = SIARDbundle;
    this.database = database;
  }

  @Override
  public MetadataTableList createTable(ViewerTable table, ViewerSchema schema) {

    List<ViewerCheckConstraint> columns = table.getCheckConstraints();
    Label header = new Label("");
    HTMLPanel info = new HTMLPanel(SafeHtmlUtils.EMPTY_SAFE_HTML);

    if (columns.isEmpty()) {
      return new MetadataTableList<>(header, messages.tableDoesNotContainConstraints());
    } else {

      return new MetadataTableList<>(header, info, columns.iterator(),
        new MetadataTableList.ColumnInfo<>(messages.name(), 15, new TextColumn<ViewerCheckConstraint>() {
          @Override
          public String getValue(ViewerCheckConstraint object) {
            return object.getName();
          }
        }), new MetadataTableList.ColumnInfo<>(messages.constraints_condition(), 15,
          new TextColumn<ViewerCheckConstraint>() {
            @Override
            public String getValue(ViewerCheckConstraint object) {
              return object.getCondition();
            }
          }),
        new MetadataTableList.ColumnInfo<>(messages.description(), 15, getDescriptionColumn(table, schema)));
    }
  }

  @Override
  public Column<ViewerCheckConstraint, String> getDescriptionColumn(ViewerTable table, ViewerSchema schema) {
    Column<ViewerCheckConstraint, String> description = new Column<ViewerCheckConstraint, String>(new EditTextCell()) {
      @Override
      public String getValue(ViewerCheckConstraint object) {
        return object.getDescription();
      }
    };

    description.setFieldUpdater((index, object, value) -> {
      object.setDescription(value);
      updateSIARDbundle(schema.getName(), table.getName(), "constraint", object.getDescription(), value);
    });

    return description;
  }

  @Override
  public void updateSIARDbundle(String schemaName, String tableName, String type, String displayName, String value) {
    SIARDbundle.setTableType(schemaName, tableName, type, displayName, value);
  }
}
