package com.r.projektnizad.models.change;

import com.r.projektnizad.models.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SerializedChange implements Serializable {
  // takes change and only saves diff
  // this is used for saving changes to database,
  // so we don't need to save whole change, just diff
  // more robust against changes since we don't need to save whole object

  private final DiffContainer<String, String> diff;

  private final User user;

  private final LocalDateTime dateTime;

  private final String entityName;

  private final Long entityId;

  public SerializedChange(DiffContainer<String, String> diff, User user, LocalDateTime dateTime, String entityName, Long entityId) {
    this.diff = diff;
    this.user = user;
    this.dateTime = dateTime;
    this.entityName = entityName;
    this.entityId = entityId;
  }

}
