/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/dbptk-ui
 */
package com.databasepreservation.common.api.exceptions;


import com.databasepreservation.common.exceptions.ViewerException;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class ApiException extends ViewerException {
  private static final long serialVersionUID = 4667937307148805083L;

  public static final int INVALID_PARAMETER_VALUE = 1;
  public static final int EMPTY_PARAMETER = 2;
  public static final int RESOURCE_ALREADY_EXISTS = 3;

  private int code;

  public ApiException(int code, String msg) {
    super(msg);
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
