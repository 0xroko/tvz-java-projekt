package com.r.projektnizad.models;

import java.io.Serializable;

public class Table extends Entity implements Serializable {
  private String name;
  private String description;
  private Long seats;

  public Table(Long id, String name, String description, Long seats) {
    super(id);
    this.name = name;
    this.description = description;
    this.seats = seats;
  }

  @Override
  public String getEntityName() {
    return "stol";
  }
}
