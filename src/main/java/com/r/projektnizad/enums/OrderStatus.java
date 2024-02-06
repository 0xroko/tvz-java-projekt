package com.r.projektnizad.enums;

import java.util.ArrayList;

public enum OrderStatus {

  /**
   * ONLY USED FOR FILTERING
   */
  ALL(-1L, "Sve"),
  RESERVED(0L, "Rezervirano"),
  DONE(1L, "Gotovo"),
  CANCELED(2L, "Otkazano"),
  IN_PROGRESS(3L, "U tijeku");

  private final String name;
  private final Long code;

  OrderStatus(Long code, String name) {
    this.name = name;
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public Long getCode() {
    return code;
  }

  public static OrderStatus fromCode(Long code) {
    for (OrderStatus status : OrderStatus.values()) {
      if (status.getCode().equals(code)) {
        return status;
      }
    }
    return null;
  }

  public static ArrayList<OrderStatus> getValues() {
    ArrayList<OrderStatus> values = new ArrayList<>();
    for (OrderStatus status : OrderStatus.values()) {
      if (!status.equals(ALL)) {
        values.add(status);
      }
    }
    return values;
  }
}
