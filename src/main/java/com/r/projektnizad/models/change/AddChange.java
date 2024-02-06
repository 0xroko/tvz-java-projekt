package com.r.projektnizad.models.change;

import com.r.projektnizad.models.Entity;

import java.io.Serial;

public class AddChange<T extends Entity> extends Change<T> {
  @Serial
  private static final long serialVersionUID = 1L;

  public AddChange(T newEntity) {
    super(null, newEntity);
  }

  @Override
  public String getChangeType() {
    return "Dodavanje";
  }
}
