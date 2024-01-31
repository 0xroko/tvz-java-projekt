package com.r.projektnizad.exceptions;

public class InvalidChangeBuild extends RuntimeException {

  public InvalidChangeBuild() {
    super();
  }

  public InvalidChangeBuild(String message) {
    super(message);
  }

  public InvalidChangeBuild(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidChangeBuild(Throwable cause) {
    super(cause);
  }

  protected InvalidChangeBuild(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
