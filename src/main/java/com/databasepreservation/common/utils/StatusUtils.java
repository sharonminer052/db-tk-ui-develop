package com.databasepreservation.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.databasepreservation.common.client.ViewerConstants;
import com.databasepreservation.common.client.models.status.collection.AdvancedStatus;
import com.databasepreservation.common.client.models.status.collection.CollectionStatus;
import com.databasepreservation.common.client.models.status.collection.ColumnStatus;
import com.databasepreservation.common.client.models.status.collection.DetailsStatus;
import com.databasepreservation.common.client.models.status.collection.ExportStatus;
import com.databasepreservation.common.client.models.status.collection.FacetsStatus;
import com.databasepreservation.common.client.models.status.collection.ListStatus;
import com.databasepreservation.common.client.models.status.collection.SearchStatus;
import com.databasepreservation.common.client.models.status.collection.TableStatus;
import com.databasepreservation.common.client.models.status.collection.TemplateStatus;
import com.databasepreservation.common.client.models.status.database.DatabaseStatus;
import com.databasepreservation.common.client.models.status.database.Indicators;
import com.databasepreservation.common.client.models.status.database.SiardStatus;
import com.databasepreservation.common.client.models.status.database.ValidationStatus;
import com.databasepreservation.common.client.models.structure.ViewerColumn;
import com.databasepreservation.common.client.models.structure.ViewerDatabase;
import com.databasepreservation.common.client.models.structure.ViewerDatabaseValidationStatus;
import com.databasepreservation.common.client.models.structure.ViewerTable;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public class StatusUtils {

  public static List<TableStatus> getTableStatusFromList(Collection<ViewerTable> tables) {
    List<TableStatus> tableStatus = new ArrayList<>();
    for (ViewerTable table : tables) {
      tableStatus.add(getTableStatus(table));
    }
    return tableStatus;
  }

  public static TableStatus getTableStatus(ViewerTable table) {
    return getTableStatus(table, true);
  }

  public static TableStatus getTableStatus(ViewerTable table, boolean show) {
    TableStatus status = new TableStatus();
    status.setUuid(table.getUuid());
    status.setId(table.getId());
    status.setName(table.getName());
    status.setCustomName(table.getName());
    status.setDescription(table.getDescription());
    status.setCustomDescription(table.getDescription());
    status.setColumns(getColumnsStatus(table.getColumns()));
    status.setShow(show);

    return status;
  }

  public static List<ColumnStatus> getColumnsStatus(List<ViewerColumn> viewerColumns) {
    return getColumnsStatus(viewerColumns, true);
  }

  public static List<ColumnStatus> getColumnsStatus(List<ViewerColumn> viewerColumns, boolean show) {
    List<ColumnStatus> columnStatusList = new ArrayList<>();
    int order = 1;
    for (ViewerColumn viewerColumn : viewerColumns) {
      columnStatusList.add(getColumnStatus(viewerColumn, show, order++));
    }
    return columnStatusList;
  }

  public static ColumnStatus getColumnStatus(ViewerColumn column, boolean show, int order) {
    ColumnStatus status = new ColumnStatus();
    status.setId(column.getSolrName());
    status.setName(column.getDisplayName());
    if (column.getType() != null) {
      status.setOriginalType(column.getType().getOriginalTypeName());
      status.setTypeName(column.getType().getTypeName());
      status.setNullable(column.getNillable().toString());
      status.setType(column.getType().getDbType());
    }
    status.setColumnIndex(column.getColumnIndexInEnclosingTable());
    status.setCustomName(column.getDisplayName());
    status.setDescription(column.getDescription());
    status.setCustomDescription(column.getDescription());
    status.setNestedColumns(null);
    status.setOrder(order);
    status.setExportStatus(getExportStatus());
    status.setSearchStatus(getSearchStatus(show));
    status.setDetailsStatus(getDetailsStatus(show));

    return status;
  }

  private static ExportStatus getExportStatus() {
    ExportStatus status = new ExportStatus();
    status.setTemplateStatus(getTemplateStatus());

    return status;
  }

  public static DetailsStatus getDetailsStatus(boolean show) {
    DetailsStatus status = new DetailsStatus();
    status.setShow(show);
    status.setTemplateStatus(getTemplateStatus());

    return status;
  }

  public static SearchStatus getSearchStatus(boolean show) {
    SearchStatus status = new SearchStatus();
    status.setAdvanced(getAdvancedStatus());
    status.setList(getListStatus(show));
    status.setFacets(getFacetsStatus());

    return status;
  }

  public static AdvancedStatus getAdvancedStatus() {
    return new AdvancedStatus();
  }

  public static ListStatus getListStatus(boolean show) {
    ListStatus status = new ListStatus();
    status.setShow(show);
    status.setTemplate(getTemplateStatus());

    return status;
  }

  public static TemplateStatus getTemplateStatus() {
    return new TemplateStatus();
  }

  public static FacetsStatus getFacetsStatus() {
    return new FacetsStatus();
  }

  public static DatabaseStatus getDatabaseStatus(ViewerDatabase database) {
    DatabaseStatus status = new DatabaseStatus();
    status.setId(database.getUuid());
    status.setValidationStatus(getValidationStatus(database));
    status.setSiardStatus(getSiardStatus(database));

    return status;
  }

  public static ValidationStatus getValidationStatus(ViewerDatabase database) {
    ValidationStatus status = new ValidationStatus();
    status.setValidatorVersion(database.getValidatedVersion());
    status.setReportLocation(database.getValidatorReportPath());
    status.setCreatedOn(database.getValidatedAt());
    status.setValidationStatus(database.getValidationStatus());
    status.setIndicators(getIndicators(database));

    return status;
  }

  public static ValidationStatus getValidationStatus(ViewerDatabaseValidationStatus validationStatus, String date,
    String validationReportPath, String dbptkVersion, Indicators indicators) {
    ValidationStatus status = new ValidationStatus();
    status.setValidationStatus(validationStatus);
    status.setCreatedOn(date);
    status.setReportLocation(validationReportPath);
    status.setValidatorVersion(dbptkVersion);
    status.setIndicators(indicators);

    return status;
  }

  public static Indicators getIndicators(ViewerDatabase database) {
    Indicators indicators = new Indicators();
    indicators.setSuccess(database.getValidationPassed());
    indicators.setFailed(database.getValidationErrors());
    indicators.setWarnings(database.getValidationWarnings());
    indicators.setSkipped(database.getValidationSkipped());

    return indicators;
  }

  public static Indicators getIndicators(String passed, String failed, String warnings, String skipped) {
    Indicators indicators = new Indicators();
    indicators.setSuccess(passed);
    indicators.setFailed(failed);
    indicators.setWarnings(warnings);
    indicators.setSkipped(skipped);

    return indicators;
  }

  public static SiardStatus getSiardStatus(ViewerDatabase database) {
    SiardStatus status = new SiardStatus();
    status.setLocation(database.getPath());

    return status;
  }

  public static CollectionStatus getCollectionStatus(final String databaseUUID, final String solrCollectionName) {
    CollectionStatus status = new CollectionStatus();
    status.setVersion(ViewerConstants.COLLECTION_STATUS_VERSION);
    status.setDatabaseUUID(databaseUUID);
    status.setSolrCollectionPrefix(ViewerConstants.SOLR_INDEX_ROW_COLLECTION_NAME_PREFIX);
    status.setId(solrCollectionName);

    return status;
  }
}
