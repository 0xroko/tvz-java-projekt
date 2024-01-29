package com.r.projektnizad.models.history;

import com.r.projektnizad.models.Entity;

public class ChangeFactory<T extends Entity, U extends Change<T>> {
  public U create(T oldEntity, T newEntity) {
    if (oldEntity == null) {
      return (U) new AddChange<T>(newEntity);
    } else if (newEntity == null) {
      return (U) new DeleteChange<T>(oldEntity);
    } else {
      return (U) new ModifyChange<T>(oldEntity, newEntity);
    }
  }

}
