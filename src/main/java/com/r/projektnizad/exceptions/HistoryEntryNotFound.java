package com.r.projektnizad.exceptions;

public class HistoryEntryNotFound extends Exception {

  public HistoryEntryNotFound() {
  }

  public HistoryEntryNotFound(String message) {
    super(message);
  }

  public HistoryEntryNotFound(String message, Throwable cause) {
    super(message, cause);
  }

  public HistoryEntryNotFound(Throwable cause) {
    super(cause);
  }

  public HistoryEntryNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
