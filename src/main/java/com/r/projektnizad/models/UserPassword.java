package com.r.projektnizad.models;

import java.util.HexFormat;

public record UserPassword(byte[] hash, byte[] salt) {
  @Override
  public String toString() {
    return HexFormat.of().formatHex(hash) + ":" + HexFormat.of().formatHex(salt);
  }

  public static UserPassword fromString(String str) {
    String[] parts = str.split(":");
    return new UserPassword(HexFormat.of().parseHex(parts[0]), HexFormat.of().parseHex(parts[1]));
  }
}
