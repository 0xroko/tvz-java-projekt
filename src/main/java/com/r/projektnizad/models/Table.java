package com.r.projektnizad.models;

import com.r.projektnizad.annotations.NamedHistoryMember;

import java.io.Serial;
import java.io.Serializable;

public class Table extends Entity implements Serializable, Cloneable {
  @Serial
  private static final long serialVersionUID = 1L;
  @NamedHistoryMember("Ime")
  private String name;
  @NamedHistoryMember("Opis")
  private String description;
  @NamedHistoryMember("Broj mjesta")
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

  public Long getSeats() {
    return seats;
  }

  public void setSeats(Long seats) {
    this.seats = seats;
  }

  @Override
  public Table clone() {
    try {
      return (Table) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
