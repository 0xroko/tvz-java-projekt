package com.r.projektnizad.enums;

public enum ItemType {
  INSTANT(0L, "Dostupno odmah"),
  PREPARABLE(1L, "Zahtjeva pripremu");

  private final String name;
  private final Long code;

  ItemType(Long code, String name) {
    this.name = name;
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public static ItemType fromCode(Long code) {
    for (ItemType itemType : ItemType.values()) {
      if (itemType.getCode().equals(code)) {
        return itemType;
      }
    }
    return null;
  }

  public Long getCode() {
    return code;
  }
}
