package com.databasepreservation.dbviewer.transformers;

import java.util.ArrayList;
import java.util.List;

import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerColumn;
import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerType;
import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerTypeArray;
import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerTypeStructure;
import com.databasepreservation.dbviewer.exceptions.ViewerException;
import com.databasepreservation.model.structure.ColumnStructure;
import com.databasepreservation.model.structure.type.ComposedTypeArray;
import com.databasepreservation.model.structure.type.ComposedTypeStructure;
import com.databasepreservation.model.structure.type.SimpleTypeBinary;
import com.databasepreservation.model.structure.type.SimpleTypeBoolean;
import com.databasepreservation.model.structure.type.SimpleTypeDateTime;
import com.databasepreservation.model.structure.type.SimpleTypeEnumeration;
import com.databasepreservation.model.structure.type.SimpleTypeInterval;
import com.databasepreservation.model.structure.type.SimpleTypeNumericApproximate;
import com.databasepreservation.model.structure.type.SimpleTypeNumericExact;
import com.databasepreservation.model.structure.type.SimpleTypeString;
import com.databasepreservation.model.structure.type.Type;
import org.joda.time.DateTimeZone;

import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerDatabase;
import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerMetadata;
import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerSchema;
import com.databasepreservation.dbviewer.client.ViewerStructure.ViewerTable;
import com.databasepreservation.dbviewer.utils.SolrUtils;
import com.databasepreservation.dbviewer.utils.ViewerUtils;
import com.databasepreservation.model.structure.DatabaseStructure;
import com.databasepreservation.model.structure.SchemaStructure;
import com.databasepreservation.model.structure.TableStructure;

/**
 * Utility class used to convert a DatabaseStructure (used in Database
 * Preservation Toolkit) to a ViewerStructure (used in Database Viewer)
 * 
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class ToolkitStructure2ViewerStructure {
  /**
   * Private empty constructor
   */
  private ToolkitStructure2ViewerStructure() {
  }

  /**
   * Deep-convert a DatabaseStructure to a ViewerDatabase
   * 
   * @param structure
   *          the database structure used by Database Preservation Toolkit
   * @return an equivalent database that can be used by Database Viewer
   */
  public static ViewerDatabase getDatabase(DatabaseStructure structure) throws ViewerException {
    ViewerDatabase result = new ViewerDatabase();
    result.setUuid(SolrUtils.randomUUID());
    result.setMetadata(getMetadata(structure));
    return result;
  }

  private static ViewerMetadata getMetadata(DatabaseStructure structure) throws ViewerException {
    ViewerMetadata result = new ViewerMetadata();
    result.setName(structure.getName());
    result.setArchivalDate(getArchivalDate(structure));
    result.setSchemas(getSchemas(structure.getSchemas()));
    return result;
  }

  private static String getArchivalDate(DatabaseStructure structure) {
    return ViewerUtils.dateToString(structure.getArchivalDate().withZone(DateTimeZone.UTC).toDate());
  }

  private static List<ViewerSchema> getSchemas(List<SchemaStructure> schemas) throws ViewerException {
    List<ViewerSchema> result = new ArrayList<>();
    for (SchemaStructure schema : schemas) {
      result.add(getSchema(schema));
    }
    return result;
  }

  private static ViewerSchema getSchema(SchemaStructure schema) throws ViewerException {
    ViewerSchema result = new ViewerSchema();
    result.setName(schema.getName());
    result.setTables(getTables(schema.getTables()));
    return result;
  }

  private static List<ViewerTable> getTables(List<TableStructure> tables) throws ViewerException {
    List<ViewerTable> result = new ArrayList<>();
    for (TableStructure table : tables) {
      result.add(getTable(table));
    }
    return result;
  }

  private static ViewerTable getTable(TableStructure table) throws ViewerException {
    ViewerTable result = new ViewerTable();
    result.setUuid(SolrUtils.randomUUID());
    result.setName(table.getName());
    result.setDescription(table.getDescription());
    result.setCountRows(table.getRows());
    result.setSchema(table.getSchema());
    result.setColumns(getColumns(table.getColumns()));
    return result;
  }

  private static List<ViewerColumn> getColumns(List<ColumnStructure> columns) throws ViewerException {
    List<ViewerColumn> result = new ArrayList<>();
    for (ColumnStructure column : columns) {
      result.add(getColumn(column));
    }
    return result;
  }

  private static ViewerColumn getColumn(ColumnStructure column) throws ViewerException {
    ViewerColumn result = new ViewerColumn();

    result.setName(column.getName());
    result.setDescription(column.getDescription());
    result.setAutoIncrement(column.getIsAutoIncrement());
    result.setDefaultValue(column.getDefaultValue());
    result.setNillable(column.getNillable());
    result.setType(getType(column.getType()));

    return result;
  }

  private static ViewerType getType(Type type) throws ViewerException {
    ViewerType result = new ViewerType();

    if(type instanceof SimpleTypeBinary){
      result.setDbType(ViewerType.dbTypes.BINARY);
    } else if(type instanceof SimpleTypeBoolean){
      result.setDbType(ViewerType.dbTypes.BOOLEAN);
    } else if(type instanceof SimpleTypeDateTime){
      result.setDbType(ViewerType.dbTypes.DATETIME);
    } else if(type instanceof SimpleTypeEnumeration){
      result.setDbType(ViewerType.dbTypes.ENUMERATION);
    } else if(type instanceof SimpleTypeInterval){
      result.setDbType(ViewerType.dbTypes.INTERVAL);
    } else if(type instanceof SimpleTypeNumericApproximate){
      result.setDbType(ViewerType.dbTypes.NUMERIC_APPROXIMATE);
    } else if(type instanceof SimpleTypeNumericExact){
      result.setDbType(ViewerType.dbTypes.NUMERIC_EXACT);
    } else if(type instanceof SimpleTypeString){
      result.setDbType(ViewerType.dbTypes.STRING);
    } else if(type instanceof ComposedTypeArray){
      result = new ViewerTypeArray();
      result.setDbType(ViewerType.dbTypes.COMPOSED_ARRAY);
      // set type of elements in the array
      ((ViewerTypeArray)result).setElementType(getType(((ComposedTypeArray)type).getElementType()));
    } else if(type instanceof ComposedTypeStructure){
      result = new ViewerTypeStructure();
      result.setDbType(ViewerType.dbTypes.COMPOSED_STRUCTURE);
    } else {
      throw new ViewerException("Unknown type: " + type.toString());
    }

    result.setDescription(type.getDescription());
    result.setTypeName(type.getSql2003TypeName());
    result.setOriginalTypeName(type.getOriginalTypeName());

    return result;
  }
}
