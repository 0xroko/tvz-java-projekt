package com.r.projektnizad.models.history;

import com.r.projektnizad.models.Entity;

public class AddChange<T extends Entity> extends Change<T> {

  public AddChange(T newEntity) {
    super(null, newEntity);
  }

  @Override
  public String getChangeType() {
    return "Dodavanje";
  }
}
