package com.r.projektnizad.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Function;

public class Filter {

  public enum FilterType {
    LIKE, EQUAL, DATE,
  }

  public static String formatLocalDateTime(LocalDateTime date) {
    return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  public static class FilterItem {
    String value;
    final FilterType type;

    public FilterItem(String value, FilterType type) {
      this.value = value;
      this.type = type;
    }

    String getValue() {
      return value;
    }

  }

  private static boolean handleOrAnd(Map.Entry<String, FilterItem> entry, StringBuilder sb, Function<String, String> function) {
    if (entry.getKey().contains("|")) {
      String[] columns = entry.getKey().split("\\|");
      sb.append("(");
      for (String column : columns) {
        sb.append(function.apply(column));
        sb.append(" OR ");
      }
      sb.delete(sb.length() - 4, sb.length());
      sb.append(") AND ");
      return true;
    } else if (entry.getKey().contains("&")) {
      String[] columns = entry.getKey().split("&");
      sb.append("(");
      for (String column : columns) {
        sb.append(function.apply(column));
        sb.append(" AND ");
      }
      sb.delete(sb.length() - 5, sb.length());
      sb.append(") AND ");
      return true;
    }
    return false;
  }

  public static String build(String baseQuery, Map<String, FilterItem> items) {
    StringBuilder sb = new StringBuilder(baseQuery);
    if (!items.isEmpty()) {
      sb.append(" WHERE ");
    } else {
      return sb.toString();
    }
    for (Map.Entry<String, FilterItem> entry : items.entrySet()) {
      if (entry.getValue().type == FilterType.LIKE) {
        if (handleOrAnd(entry, sb, column -> column + " LIKE '%" + entry.getValue().getValue() + "%'")) {
          continue;
        } else {
          sb.append(entry.getKey());
          sb.append(" LIKE ");
          sb.append("'%");
          sb.append(entry.getValue().getValue());
          sb.append("%'");
        }
      } else if (entry.getValue().type == FilterType.EQUAL) {
        if (handleOrAnd(entry, sb, column -> column + " = '" + entry.getValue().getValue() + "'")) {
          continue;
        } else {
          sb.append(entry.getKey());
          sb.append(" = ");
          sb.append("'");
          sb.append(entry.getValue().getValue());
          sb.append("'");
        }
      } else if (entry.getValue().type == FilterType.DATE) {
        // TS >= DATE('2009-10-01') AND TS < (DATE('2009-10-01') + INTERVAL 1 DAY)
        sb.append(entry.getKey());
        sb.append(" >= DATE('");
        sb.append(entry.getValue().getValue());
        sb.append("') AND ");
        sb.append(entry.getKey());
        sb.append(" < (DATE('");
        sb.append(entry.getValue().getValue());
        sb.append("') + INTERVAL 1 DAY)");
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
