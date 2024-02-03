package com.r.projektnizad.enums;

public enum ItemOnOrderStatus {
  PREPARING(0L, "U pripremi"),
  DONE(1L, "Gotovo");

  private final String name;
  private final Long code;

  ItemOnOrderStatus(Long code, String name) {
    this.name = name;
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public Long getCode() {
    return code;
  }

  public static ItemOnOrderStatus fromCode(Long code) {
    for (ItemOnOrderStatus status : ItemOnOrderStatus.values()) {
      if (status.getCode().equals(code)) {
        return status;
      }
    }
    return null;
  }

}
