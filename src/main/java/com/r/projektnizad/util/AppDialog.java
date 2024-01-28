package com.r.projektnizad.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AppDialog {

  public void showErrorMessage(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);

    alert.setTitle(title);
    alert.setContentText(message);

    alert.showAndWait();
  }

  public ButtonType showConfirmationMessage(String title, String message, ButtonType buttonType) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

    alert.setTitle(title);
    alert.setContentText(message);
    alert.getButtonTypes().setAll(buttonType, CustomButtonTypes.CANCEL);

    return alert.showAndWait().orElse(CustomButtonTypes.CANCEL);
  }


}
