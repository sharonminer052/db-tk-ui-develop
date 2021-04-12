/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/dbptk-ui
 */
package com.databasepreservation.common.client.common.lists.columns;

import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;

/**
 * Column used for dynamic table
 *
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public class IndexedColumn extends Column<List<String>, String> {
  private final int index;

  public IndexedColumn(int index) {
    super(new TextCell());
    this.index = index;
  }

  @Override
  public String getValue(List<String> object) {
    return object.get(index);
  }

  public int getIndex() {
    return this.index;
  }
}
