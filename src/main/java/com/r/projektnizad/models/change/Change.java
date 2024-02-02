package com.r.projektnizad.models.change;

import com.r.projektnizad.decorators.NamedHistoryMember;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.Entity;
import com.r.projektnizad.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class Change<T extends Entity> implements Serializable {
  T oldEntity, newEntity;
  LocalDateTime dateTime;
  User user;

  public final Long serialVersionUID = 9043873L;

  private static final Logger logger = LoggerFactory.getLogger(Change.class);

  public Change(T oldEntity, T newEntity) {
    this.oldEntity = oldEntity;
    this.newEntity = newEntity;
    this.dateTime = LocalDateTime.now();
    this.user = Main.authService.getCurrentUser().orElse(null);
  }

  public Map<String, String> diff() {
    return diff(false, "");
  }

  public Map<String, String> diff(boolean onlyName, String prefix) {
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

        // is annotation present
        boolean hasCustomName = field.isAnnotationPresent(NamedHistoryMember.class);

        if (onlyName && !field.getName().equals("name")) continue;
        if (!oldValue.equals(newValue)) {
          // if values are not primitive types, recursively call diff
          if (oldValue instanceof Entity) {
            if (newValue == null) continue;
            String entityName = ((Entity) oldValue).getEntityName();
            diff.putAll(((Change<?>) new ModifyChange<>((Entity) oldValue, (Entity) newValue)).diff(true, prefix + entityName + " "));
            continue;
          }

          if (onlyName) {
            diff.put(prefix, oldValue + " -> " + newValue);
          } else {
            String name = hasCustomName ? field.getAnnotation(NamedHistoryMember.class).value() : field.getName();
            diff.put(name, oldValue + " -> " + newValue);
          }
        }
      } catch (IllegalAccessException e) {
        logger.error("Error while accessing field", e);
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
