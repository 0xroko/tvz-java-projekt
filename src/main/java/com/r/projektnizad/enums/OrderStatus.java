package com.r.projektnizad.enums;

public enum OrderStatus {
  RESERVED("Reserved"),
  DONE("Done"),
  CANCELED("Canceled"),
  IN_PROGRESS("In progress");


  private String name;

  OrderStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
