/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.exceptions;

public class InvalidPasswordException extends Exception {
  public InvalidPasswordException() {
    super();
  }

  public InvalidPasswordException(String message) {
    super(message);
  }

  public InvalidPasswordException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidPasswordException(Throwable cause) {
    super(cause);
  }

  public InvalidPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
