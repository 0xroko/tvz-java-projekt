package com.r.projektnizad.exceptions;

public class DatabaseActionFailException extends Exception {

  public DatabaseActionFailException() {
  }

  public DatabaseActionFailException(String message) {
    super(message);
  }

  public DatabaseActionFailException(String message, Throwable cause) {
    super(message, cause);
  }

  public DatabaseActionFailException(Throwable cause) {
    super(cause);
  }

  public DatabaseActionFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
