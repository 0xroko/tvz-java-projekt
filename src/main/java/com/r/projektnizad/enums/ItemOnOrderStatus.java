package com.r.projektnizad.enums;

public enum ItemOnOrderStatus {
  PREPARING("Preparing"),
  DONE("Done");

  private String name;

  ItemOnOrderStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
