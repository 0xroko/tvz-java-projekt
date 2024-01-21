package com.r.projektnizad.util;

import javafx.scene.control.Alert;

public class AppDialog {

  public void showErrorMessage(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);

    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);

    alert.showAndWait();
  }
}
