package com.r.projektnizad.util;

import java.util.Map;

public class Filter {

  public static class FilterItem {
    String value;

    boolean like = false;

    public FilterItem(String value, boolean like) {
      this.value = value;
      this.like = like;
    }

    String getValue() {
      return value;
    }

    void setValue(String value) {
      this.value = value;
    }

  }

  public static String build(String baseQuery, Map<String, FilterItem> items) {
    StringBuilder sb = new StringBuilder(baseQuery);
    if (!items.isEmpty()) {
      sb.append(" WHERE ");
    } else {
      return sb.toString();
    }
    for (Map.Entry<String, FilterItem> entry : items.entrySet()) {
      sb.append(entry.getKey());

      if (entry.getValue().like) {
        sb.append(" LIKE ");
        sb.append("'%");
        sb.append(entry.getValue().getValue());
        sb.append("%'");
      } else {
        sb.append(" = ");
        sb.append("'");
        sb.append(entry.getValue().getValue());
        sb.append("'");
      }

      sb.append(" AND ");
    }
    if (!items.isEmpty()) {
      sb.delete(sb.length() - 5, sb.length());
    } else {
      sb.delete(sb.length() - 7, sb.length());
    }

    return sb.toString();
  }
}
