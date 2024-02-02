package com.r.projektnizad.models;

import com.r.projektnizad.decorators.NamedHistoryMember;

import java.io.Serializable;

public class Category extends Entity implements Serializable, Cloneable {
  @NamedHistoryMember("Naziv")
  private String name;
  @NamedHistoryMember("Opis")
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

  @Override
  public Category clone() {
    try {
      return (Category) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
