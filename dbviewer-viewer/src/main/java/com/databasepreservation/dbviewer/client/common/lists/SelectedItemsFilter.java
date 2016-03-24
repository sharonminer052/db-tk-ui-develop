package com.databasepreservation.dbviewer.client.common.lists;

import org.roda.core.data.adapter.filter.Filter;
import org.roda.core.data.v2.index.IsIndexed;

public class SelectedItemsFilter<T extends IsIndexed> implements SelectedItems<T> {

  private static final long serialVersionUID = 975693329806484985L;

  private Filter filter;

  public SelectedItemsFilter() {
    super();
  }

  public SelectedItemsFilter(Filter filter) {
    super();
    this.filter = filter;
  }

  public Filter getFilter() {
    return filter;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((filter == null) ? 0 : filter.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SelectedItemsFilter<T> other = (SelectedItemsFilter<T>) obj;
    if (filter == null) {
      if (other.filter != null)
        return false;
    } else if (!filter.equals(other.filter))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SelectedItemsFilter [filter=" + filter + "]";
  }

}
