package com.r.projektnizad.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/r/projektnizad/fxml/hello-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 320*2, 240*2);
    stage.setTitle("Hello!");
    stage.setScene(scene);

    // set stylesheet
    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/r/projektnizad/styles/styles.css")).toExternalForm());
    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/r/projektnizad/styles/fontstyle.css")).toExternalForm());

    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }
}
