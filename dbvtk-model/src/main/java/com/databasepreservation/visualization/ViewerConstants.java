package com.databasepreservation.visualization;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;

/**
 * Non GWT-safe constants used in Database Viewer.
 *
 * @see com.databasepreservation.visualization.shared.ViewerSafeConstants for
 *      the GWT-safe constants
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class ViewerConstants {
  /*
   * DBVTK CONFIG
   */
  public static final String INSTALL_FOLDER_SYSTEM_PROPERTY = "dbvtk.home";
  public static final String INSTALL_FOLDER_ENVIRONMENT_VARIABLE = "DBVTK_HOME";
  public static final String INSTALL_FOLDER_DEFAULT_SUBFOLDER_UNDER_HOME = ".db-visualization-toolkit";

  public static final String DEFAULT_ENCODING = "UTF-8";
  public static final String VIEWER_CONFIG_FOLDER = "config";
  public static final String CORE_LOG_FOLDER = "log";
  public static final String VIEWER_EXAMPLE_CONFIG_FOLDER = "example-config";
  public static final String VIEWER_I18N_FOLDER = "i18n";
  public static final String VIEWER_LOG_FOLDER = "log";
  public static final String VIEWER_LOBS_FOLDER = "lobs";
}
