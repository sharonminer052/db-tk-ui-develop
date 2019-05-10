/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package com.databasepreservation.visualization.server.index.schema;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.roda.core.data.exceptions.AuthorizationDeniedException;
import org.roda.core.data.exceptions.GenericException;
import org.roda.core.data.exceptions.NotFoundException;
import org.roda.core.data.exceptions.NotSupportedException;
import org.roda.core.data.exceptions.RequestNotValidException;

import com.databasepreservation.visualization.exceptions.ViewerException;
import com.databasepreservation.visualization.shared.ViewerStructure.IsIndexed;
import com.databasepreservation.visualization.server.index.schema.collections.DatabasesCollection;
import com.databasepreservation.visualization.server.index.schema.collections.SavedSearchesCollection;

public final class SolrDefaultCollectionRegistry {

  private SolrDefaultCollectionRegistry() {

  }

  private static final Map<Class<? extends IsIndexed>, SolrCollection<? extends IsIndexed>> REGISTRY = new HashMap<>();

  static {
    register(new DatabasesCollection());
    register(new SavedSearchesCollection());
  }

  public static <T extends IsIndexed> void register(SolrCollection<T> collection) {
    REGISTRY.put(collection.getObjectClass(), collection);
  }

  public static Collection<SolrCollection<? extends IsIndexed>> registry() {
    return Collections.unmodifiableCollection(REGISTRY.values());
  }

  public static List<String> registryIndexNames() {
    return registry().stream().map(col -> col.getIndexName()).collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  public static <I extends IsIndexed> SolrCollection<I> get(Class<I> indexClass) {
    return (SolrCollection<I>) REGISTRY.get(indexClass);
  }

  public static <I extends IsIndexed> I fromSolrDocument(Class<I> indexClass, SolrDocument doc,
    List<String> fieldsToReturn) throws ViewerException, NotSupportedException {
    SolrCollection<I> solrCollection = get(indexClass);
    if (solrCollection != null) {
      return solrCollection.fromSolrDocument(doc);
    } else {
      throw new NotSupportedException(
        "Could not find Solr collection relative to '" + indexClass.getName() + "' in registry.");
    }
  }

  public static <I extends IsIndexed> I fromSolrDocument(Class<I> indexClass, SolrDocument doc)
    throws ViewerException, NotSupportedException {
    return fromSolrDocument(indexClass, doc, Collections.emptyList());
  }

  public static <I extends IsIndexed> SolrInputDocument toSolrDocument(Class<I> indexClass, I object)
    throws NotSupportedException, ViewerException, AuthorizationDeniedException, NotFoundException, GenericException,
    RequestNotValidException {
    SolrCollection<I> solrCollection = get(indexClass);
    if (solrCollection != null) {
      return solrCollection.toSolrDocument(object);
    } else {
      throw new NotSupportedException(
        "Could not find Solr collection relative to '" + indexClass.getName() + "' in registry.");
    }
  }

  public static <I extends IsIndexed> String getIndexNameZZZZZZZZZZ(Class<I> indexClass) throws NotSupportedException {
    SolrCollection<I> solrCollection = get(indexClass);
    if (solrCollection != null) {
      return solrCollection.getIndexName();
    } else {
      throw new NotSupportedException(
        "Could not find Solr collection relative to '" + indexClass.getName() + "' in registry.");
    }
  }

  public static <I extends IsIndexed> List<String> getCommitIndexNames(Class<I> indexClass)
    throws NotSupportedException {
    SolrCollection<I> solrCollection = get(indexClass);
    if (solrCollection != null) {
      return solrCollection.getCommitIndexNames();
    } else {
      throw new NotSupportedException(
        "Could not find Solr collection relative to '" + indexClass.getName() + "' in registry.");
    }
  }
}