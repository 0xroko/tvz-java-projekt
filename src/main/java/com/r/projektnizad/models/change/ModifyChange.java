package com.r.projektnizad.models.change;

import com.r.projektnizad.models.Entity;

import java.io.Serial;

public class ModifyChange<T extends Entity> extends Change<T> {
  @Serial
  private static final long serialVersionUID = 1L;

  public ModifyChange(T oldEntity, T newEntity) {
    super(oldEntity, newEntity);
  }

  @Override
  public String getChangeType() {
    return "Uređivanje";
  }
}
