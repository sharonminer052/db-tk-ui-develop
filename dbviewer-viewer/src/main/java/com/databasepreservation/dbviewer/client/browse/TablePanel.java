package com.databasepreservation.dbviewer.client.browse;

import com.google.gwt.user.client.ui.Widget;
import org.roda.core.data.v2.index.IsIndexed;

import com.databasepreservation.dbviewer.client.BrowserService;
import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerDatabase;
import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerTable;
import com.databasepreservation.dbviewer.client.common.lists.TableRowList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class TablePanel extends Composite {
  interface TablePanelUiBinder extends UiBinder<Widget, TablePanel> {
  }

  @UiField
  SimplePanel container;

  private ViewerDatabase database;
  private ViewerTable table;

  private TableRowList tableRowList;

  private static TablePanelUiBinder ourUiBinder = GWT.create(TablePanelUiBinder.class);

  public TablePanel(final String databaseUUID, final String tableUUID) {
    initWidget(ourUiBinder.createAndBindUi(this));

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

          init();
        }
      });
  }

  private void init() {
    tableRowList = new TableRowList(database, table);
    container.setWidget(tableRowList);
  }
}
