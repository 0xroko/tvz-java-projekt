package com.r.projektnizad.models.change;

import com.r.projektnizad.models.Entity;

import java.io.Serial;

public class DeleteChange<T extends Entity> extends Change<T> {
  @Serial
  private static final long serialVersionUID = 1L;

  public DeleteChange(T oldEntity) {
    super(oldEntity, null);
  }

  @Override
  public String getChangeType() {
    return "Brisanje";
  }
}
