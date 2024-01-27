package com.r.projektnizad.enums;

public enum ItemType {
  INSTANT("Instant"),
  PREPARABLE("Preparable");

  private final String name;

  ItemType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
