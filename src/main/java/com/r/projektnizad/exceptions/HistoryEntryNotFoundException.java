package com.r.projektnizad.exceptions;

public class HistoryEntryNotFoundException extends Exception {

  public HistoryEntryNotFoundException() {
  }

  public HistoryEntryNotFoundException(String message) {
    super(message);
  }

  public HistoryEntryNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public HistoryEntryNotFoundException(Throwable cause) {
    super(cause);
  }

  public HistoryEntryNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
