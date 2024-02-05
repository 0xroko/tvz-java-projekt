/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.exceptions;

public class UsersNotLoadedError extends Error {

  public UsersNotLoadedError() {
    super();
  }

  public UsersNotLoadedError(String message) {
    super(message);
  }

  public UsersNotLoadedError(String message, Throwable cause) {
    super(message, cause);
  }

  public UsersNotLoadedError(Throwable cause) {
    super(cause);
  }

  protected UsersNotLoadedError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
