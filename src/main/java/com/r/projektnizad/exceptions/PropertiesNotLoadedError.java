/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.exceptions;

public class PropertiesNotLoadedError extends Error {

  public PropertiesNotLoadedError() {
    super();
  }

  public PropertiesNotLoadedError(String message) {
    super(message);
  }

  public PropertiesNotLoadedError(String message, Throwable cause) {
    super(message, cause);
  }

  public PropertiesNotLoadedError(Throwable cause) {
    super(cause);
  }

  protected PropertiesNotLoadedError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
