package com.r.projektnizad.util;

import com.r.projektnizad.main.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class Navigator {

  private static final Logger logger = LoggerFactory.getLogger(Navigator.class);
  static final public String baseResourcePath = "/com/r/projektnizad/";

  static public Stage rootStage;

  static void applyStyles(Scene scene) {
    // set stylesheet
    scene.getStylesheets().add(Objects.requireNonNull(Navigator.class.getResource("/com/r/projektnizad/styles/styles.css")).toExternalForm());
    scene.getStylesheets().add(Objects.requireNonNull(Navigator.class.getResource("/com/r/projektnizad/styles/fontstyle.css")).toExternalForm());
  }

  static public void navigate(String resourcePath, String title) {
    try {
      var window = (Parent) FXMLLoader.load(Objects.requireNonNull(Main.class.getResource(baseResourcePath + "fxml/" + resourcePath)));
      Scene scene = new Scene(window);
      applyStyles(scene);
      rootStage.setScene(scene);
      rootStage.setTitle(title);
      rootStage.show();
    } catch (IOException e) {
      logger.error("Error while loading window: " + resourcePath, e);
    }
  }
}
