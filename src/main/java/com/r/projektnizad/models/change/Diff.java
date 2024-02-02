/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.models.change;

public class Diff<T> {
  private final T oldValue;
  private final T newValue;

  public Diff(String fieldName, T oldValue, T newValue) {
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public T getOldValue() {
    return oldValue;
  }

  public T getNewValue() {
    return newValue;
  }
}
