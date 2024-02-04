package com.r.projektnizad.util;

import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.CleanableScene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class Navigator {
  private static final Logger logger = LoggerFactory.getLogger(Navigator.class);
  static final public String baseResourcePath = "/com/r/projektnizad/";
  static public Stage rootStage;
  public static ThemeManager themeManager = new ThemeManager();
  static Object controller;

  public static void setController(Object controller) {
    Navigator.controller = controller;
  }

  static void applyStyles(Scene scene) {
    // set stylesheet
    // scene.getStylesheets().add(Objects.requireNonNull(Navigator.class.getResource("/com/r/projektnizad/styles/styles.css")).toExternalForm());
    //  scene.getStylesheets().add(Objects.requireNonNull(Navigator.class.getResource("/com/r/projektnizad/styles/fontstyle.css")).toExternalForm());
  }

  static public FXMLLoader load(String resourcePath) {
    return new FXMLLoader(Objects.requireNonNull(Main.class.getResource(baseResourcePath + "fxml/" + resourcePath)));
  }

  static public Parent loadParent(String resourcePath) {
    try {
      return load(resourcePath).load();
    } catch (IOException e) {
      logger.error("Error loading resource: " + resourcePath);
      return null;
    }
  }

  static void cleanUpLastScene() {
    if (controller instanceof CleanableScene sc) {
      sc.cleanup();
    }
  }

  static public void navigate(String resourcePath, String title) {
    cleanUpLastScene();
    var window = loadParent(resourcePath);
    Scene scene = new Scene(window);
    applyStyles(scene);

    // run stop method on previous view
    if (rootStage == null) {
      logger.error("Root stage is not null");
      return;
    }

    themeManager.setScene(scene);
    rootStage.setScene(scene);
    rootStage.setTitle(title);
    rootStage.show();

    // set exception handler
    Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
      logger.error("Uncaught exception", e);
      new AppDialog().showExceptionMessage(e);
    });

    rootStage.onHidingProperty().set(e -> {
      cleanUpLastScene();
      System.exit(0);
    });
  }

  static public <T extends Dialog<?>> void asDialog(String resourcePath, Dialog<?> controller) {
    FXMLLoader l = load(resourcePath);
    l.setController(controller);

    controller.setWidth(Config.WINDOW_WIDTH);
    controller.setHeight(Config.WINDOW_HEIGHT);

    try {
      controller.getDialogPane().setContent(l.load());
    } catch (IOException e) {
      logger.error("Error loading resource: " + resourcePath, e);
    }
    applyStyles(controller.getDialogPane().getScene());

    // add on close listener to clean up
    controller.setOnCloseRequest(e -> {
      if (controller instanceof CleanableScene sc) {
        sc.cleanup();
      }
    });
  }
}
