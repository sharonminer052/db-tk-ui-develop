package com.databasepreservation.dbviewer.client.ViewerStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class ViewerSchema implements Serializable {
  private String uuid;

  private String name;

  private String description;

  private List<ViewerTable> tables;

  // private List<DbvView> views;

  // private List<DbvRoutine> routines;

  // private List<DbvComposedType> userDefinedTypes;

  public ViewerSchema() {
    tables = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ViewerTable> getTables() {
    return tables;
  }

  public void setTables(List<ViewerTable> tables) {
    this.tables = tables;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUUID() {
    return uuid;
  }

  public void setUUID(String uuid) {
    this.uuid = uuid;
  }
}
