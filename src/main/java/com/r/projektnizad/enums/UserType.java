package com.r.projektnizad.enums;

public enum UserType {
  ADMIN(0,"Admin"),
  MANAGER(1,"Manager"),
  WAITER(2,"Waiter"),
  COOK(3,"Cook");

  private final String name;

  public String getName() {
    return name;
  }

  public Integer getCode() {
    return code;
  }

  private final Integer code;

  UserType(Integer code,String name) {
    this.code = code;
    this.name = name;
  }

  public static UserType fromCode(Integer code) {
    for (UserType type : UserType.values()) {
      if (type.code.equals(code)) {
        return type;
      }
    }
    return null;
  }
}
