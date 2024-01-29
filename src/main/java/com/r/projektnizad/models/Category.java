package com.r.projektnizad.models;

import java.io.Serializable;

public class Category extends Entity implements Serializable {
  private String name;
  private String description;

  public Category(Long id, String name, String description) {
    super(id);
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getEntityName() {
    return "kategorija";
  }
}
