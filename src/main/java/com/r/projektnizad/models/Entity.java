package com.r.projektnizad.models;

import com.r.projektnizad.decorators.NamedHistoryMember;

import java.io.Serializable;

public abstract class Entity implements Serializable {
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
