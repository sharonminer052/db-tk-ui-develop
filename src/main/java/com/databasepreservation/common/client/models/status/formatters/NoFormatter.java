/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/dbptk-ui
 */
package com.databasepreservation.common.client.models.status.formatters;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
@JsonTypeName("noFormatter")
public class NoFormatter implements Formatter {

  public NoFormatter() {
  }
}
