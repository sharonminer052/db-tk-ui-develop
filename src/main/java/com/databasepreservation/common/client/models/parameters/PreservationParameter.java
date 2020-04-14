package com.databasepreservation.common.client.models.parameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public class PreservationParameter implements Serializable {

  private String name = null;
  private String description = null;
  private boolean required = false;
  private boolean hasArgument = false;
  private String inputType = null;
  private String fileFilter = null;
  private String exportOption = null;
  private String defaultValue = null;
  private List<String> possibleValues = new ArrayList<>();

  public PreservationParameter() {
  }

  public PreservationParameter(String name, String description, boolean required,
    boolean hasArgument, String inputType) {
    this.name = name;
    this.description = description;
    this.required = required;
    this.hasArgument = hasArgument;
    this.inputType = inputType;
  }

  public PreservationParameter(String name, String description, boolean required,
    boolean hasArgument, String inputType, String exportOption) {
    this.name = name;
    this.description = description;
    this.required = required;
    this.hasArgument = hasArgument;
    this.inputType = inputType;
    this.exportOption = exportOption;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public boolean hasArgument() {
    return hasArgument;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public void setHasArgument(boolean hasArgument) {
    this.hasArgument = hasArgument;
  }

  public String getInputType() {
    return inputType;
  }

  public void setInputType(String inputType) {
    this.inputType = inputType;
  }

  public String getExportOption() {
    return exportOption;
  }

  public void setExportOption(String exportOption) {
    this.exportOption = exportOption;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getFileFilter() {
    return fileFilter;
  }

  public void setFileFilter(String fileFilter) {
    this.fileFilter = fileFilter;
  }

  public List<String> getPossibleValues() {
    return possibleValues;
  }

  public void setPossibleValues(List<String> possibleValues) {
    this.possibleValues = possibleValues;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PreservationParameter that = (PreservationParameter) o;
    return isRequired() == that.isRequired() &&
        hasArgument() == that.hasArgument() &&
        Objects.equals(getName(), that.getName()) &&
        Objects.equals(getDescription(), that.getDescription());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getName(), getDescription(), isRequired(), hasArgument());
  }

  @Override
  public String toString() {
    return "PreservationParameter{" +
        "name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", required=" + required +
        ", hasArgument=" + hasArgument +
        '}';
  }
}
