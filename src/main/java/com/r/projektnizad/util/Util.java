package com.r.projektnizad.util;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.util.converter.LocalDateStringConverter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.util.function.Function;

public class Util {
  public static String formatDuration(Duration duration) {
    return String.format("%02d:%02d", duration.toHours(), duration.toMinutesPart());
  }

  public static Duration parseDuration(String duration) {
    String[] split = duration.split(":");
    return Duration.ofHours(Long.parseLong(split[0])).plusMinutes(Long.parseLong(split[1]));
  }

  public static String formatDateTime(LocalDateTime dateTime) {
    return dateTime.format(new DateTimeFormatterBuilder().appendPattern("E, dd.MM.yyyy HH:mm").toFormatter());
  }

  public static String formatTime(LocalDateTime dateTime) {
    return dateTime.format(new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter());
  }

  public static LocalDateTime parseTime(String time) {
    return LocalDateTime.parse(time, new DateTimeFormatterBuilder().appendPattern("HH:mm").toFormatter());
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

  public static <T, A> void tableViewCellFactory(TableColumn<T, A> column, Function<A, String> predicate) {
    column.setCellFactory(param -> new TableCell<>() {
      @Override
      protected void updateItem(A item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
          setText(predicate.apply(item));
        }
      }
    });
  }


  public static <T> void comboBoxCellFactorySetters(ComboBox<T> comboBox, Function<T, String> predicate) {
    comboBox.setButtonCell(getComboBoxListCell(predicate));
    comboBox.setCellFactory(param -> getComboBoxListCell(predicate));
  }
}
