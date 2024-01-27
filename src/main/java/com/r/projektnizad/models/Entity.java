package com.r.projektnizad.models;

public abstract class Entity {
  public Long id;

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
