package com.r.projektnizad.util;

import javafx.scene.control.cell.ComboBoxListCell;

import java.time.Duration;
import java.util.function.Function;

public class Util {
  public static String formatDuration(Duration duration) {
    return String.format("%02d:%02d", duration.toHours(), duration.toMinutesPart());
  }

  public static Duration parseDuration(String duration) {
    String[] split = duration.split(":");
    return Duration.ofHours(Long.parseLong(split[0])).plusMinutes(Long.parseLong(split[1]));
  }

  public static <T> ComboBoxListCell<T> getComboBoxListCell(Function<T, String> predicate) {
    return new ComboBoxListCell<>() {
      @Override
      public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
          setText(predicate.apply(item));
        }
      }
    };
  }
}
