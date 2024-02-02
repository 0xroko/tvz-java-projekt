package com.r.projektnizad.models.change;

import com.r.projektnizad.models.Entity;

public class ModifyChange<T extends Entity> extends Change<T> {
  public ModifyChange(T oldEntity, T newEntity) {
    super(oldEntity, newEntity);
  }

  @Override
  public String getChangeType() {
    return "Uređivanje";
  }
}
