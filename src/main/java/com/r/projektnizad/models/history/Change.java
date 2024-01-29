package com.r.projektnizad.models.history;

import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.Entity;
import com.r.projektnizad.models.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class Change<T extends Entity> implements Serializable {
  T oldEntity, newEntity;
  LocalDateTime dateTime;
  User user;

  public Change(T oldEntity, T newEntity) {
    this.oldEntity = oldEntity;
    this.newEntity = newEntity;
    this.dateTime = LocalDateTime.now();
    this.user = Main.authService.getCurrentUser().orElse(null);
  }

  public Map<String, String> diff() {
    Map<String, String> diff = new HashMap<>();

    if (oldEntity == null) {
      diff.put("id", "null -> " + newEntity.getId());
      return diff;
    }

    if (newEntity == null) {
      diff.put("id", oldEntity.getId() + " -> null");
      return diff;
    }

    // check all members of oldEntity and newEntity
    // if they are different, add them to diff map
    for (var field : oldEntity.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        Object oldValue = field.get(oldEntity);
        Object newValue = field.get(newEntity);
        if (!oldValue.equals(newValue)) {
          diff.put(field.getName(), oldValue + " -> " + newValue);
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    return diff;
  }

  abstract public String getChangeType();

  public T getOldEntity() {
    return oldEntity;
  }

  public void setOldEntity(T oldEntity) {
    this.oldEntity = oldEntity;
  }

  public T getNewEntity() {
    return newEntity;
  }

  public T getActualEntity() {
    return newEntity == null ? oldEntity : newEntity;
  }

  public void setNewEntity(T newEntity) {
    this.newEntity = newEntity;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
