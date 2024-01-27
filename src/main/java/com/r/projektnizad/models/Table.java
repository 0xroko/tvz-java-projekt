package com.r.projektnizad.models;

public class Table extends Entity {
  private String name;
  private String description;
  private Long seats;

  public Table(Long id, String name, String description, Long seats) {
    super(id);
    this.name = name;
    this.description = description;
    this.seats = seats;
  }
}
