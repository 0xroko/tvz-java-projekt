package com.r.projektnizad.enums;

import java.util.Arrays;
import java.util.List;

public enum UserType {
  DELETE(-999, "[izbrisan]"),
  ALL(-1, "Svi"),
  ADMIN(0, "Admin"),
  //MANAGER(1, "Menadžer"),
  WAITER(2, "Konobar");
  //COOK(3, "Kuhar");

  private final String name;

  public String getName() {
    return name;
  }

  public Integer getCode() {
    return code;
  }

  private final Integer code;

  UserType(Integer code, String name) {
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

  public static List<UserType> getValues() {
    return Arrays.stream(UserType.values()).filter(type -> type.getCode() >= 0).toList();
  }

  // get filterable values
  public static List<UserType> getFilterableValues() {
    return Arrays.stream(UserType.values()).filter(type -> type.getCode() >= -1).toList();
  }
}
