package com.r.projektnizad.models.change;

import com.r.projektnizad.models.Entity;

public class DeleteChange<T extends Entity> extends Change<T> {
  public DeleteChange(T oldEntity) {
    super(oldEntity, null);
  }

  @Override
  public String getChangeType() {
    return "Brisanje";
  }
}
