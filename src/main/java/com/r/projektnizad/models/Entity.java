package com.r.projektnizad.models;

import com.r.projektnizad.annotations.NamedHistoryMember;

import java.io.Serial;
import java.io.Serializable;

public abstract class Entity implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;
  @NamedHistoryMember("Identifikator")
  private Long id;

  public abstract String getEntityName();

  public Entity(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
