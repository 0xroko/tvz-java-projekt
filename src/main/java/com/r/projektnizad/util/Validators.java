package com.r.projektnizad.util;

import javafx.scene.control.TextField;

public class Validators {
  public static String number(int whole, int fraction) {
    StringBuilder regex = new StringBuilder();
    regex.append("[0-9]{0,").append(whole).append("}");
    if (fraction > 0) {
      regex.append("\\.?[0-9]{0,").append(fraction).append("}");
    }
    return regex.toString();
  }

  public static final String time = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

  public static void buildTextFieldValidator(TextField input, String regex) {
    input.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.matches(regex)) {
        input.setText(newValue);
      } else {
        input.setText(oldValue);
      }
    });
  }
}

