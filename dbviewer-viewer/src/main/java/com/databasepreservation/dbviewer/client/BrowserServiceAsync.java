package com.databasepreservation.dbviewer.client;

import java.util.List;

import org.roda.core.data.v2.index.IsIndexed;

import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerTable;
import com.databasepreservation.dbviewer.client.common.search.SearchField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BrowserServiceAsync {

  /**
   * GWT-RPC service asynchronous (client-side) interface
   * 
   * @see com.databasepreservation.dbviewer.client.BrowserService
   */
  <T extends IsIndexed> void find(java.lang.String classNameToReturn, org.roda.core.data.adapter.filter.Filter filter,
    org.roda.core.data.adapter.sort.Sorter sorter, org.roda.core.data.adapter.sublist.Sublist sublist,
    org.roda.core.data.adapter.facet.Facets facets, java.lang.String localeString,
    AsyncCallback<org.roda.core.data.v2.index.IndexResult<T>> callback);

  /**
   * GWT-RPC service asynchronous (client-side) interface
   * 
   * @see com.databasepreservation.dbviewer.client.BrowserService
   */
  void count(java.lang.String classNameToReturn, org.roda.core.data.adapter.filter.Filter filter,
    AsyncCallback<java.lang.Long> callback);

  /**
   * GWT-RPC service asynchronous (client-side) interface
   * 
   * @see com.databasepreservation.dbviewer.client.BrowserService
   */
  <T extends IsIndexed> void retrieve(java.lang.String classNameToReturn, java.lang.String id, AsyncCallback<T> callback);

  /**
   * GWT-RPC service asynchronous (client-side) interface
   * 
   * @see com.databasepreservation.dbviewer.client.BrowserService
   */
  <T extends IsIndexed> void findRows(java.lang.String classNameToReturn, java.lang.String tableUUID,
    org.roda.core.data.adapter.filter.Filter filter, org.roda.core.data.adapter.sort.Sorter sorter,
    org.roda.core.data.adapter.sublist.Sublist sublist, org.roda.core.data.adapter.facet.Facets facets,
    java.lang.String localeString, AsyncCallback<org.roda.core.data.v2.index.IndexResult<T>> callback);

  /**
   * GWT-RPC service asynchronous (client-side) interface
   * 
   * @see com.databasepreservation.dbviewer.client.BrowserService
   */
  void countRows(java.lang.String classNameToReturn, java.lang.String tableUUID,
    org.roda.core.data.adapter.filter.Filter filter, AsyncCallback<java.lang.Long> callback);

  /**
   * GWT-RPC service asynchronous (client-side) interface
   * 
   * @see com.databasepreservation.dbviewer.client.BrowserService
   */
  <T extends IsIndexed> void retrieveRows(java.lang.String classNameToReturn, java.lang.String tableUUID,
    java.lang.String rowUUID, AsyncCallback<T> callback);

  void getSearchFields(ViewerTable viewerTable, AsyncCallback<List<SearchField>> async);

  /**
   * Utility class to get the RPC Async interface from client-side code
   */
  public static final class Util {
    private static BrowserServiceAsync instance;

    public static final BrowserServiceAsync getInstance() {
      if (instance == null) {
        instance = (BrowserServiceAsync) GWT.create(BrowserService.class);
      }
      return instance;
    }

    private Util() {
      // Utility class should not be instantiated
    }
  }
}
