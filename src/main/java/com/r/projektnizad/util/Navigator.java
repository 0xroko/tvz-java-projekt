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
import java.util.Optional;

public class Navigator {
  private static final Logger logger = LoggerFactory.getLogger(Navigator.class);
  static final public String baseResourcePath = "/com/r/projektnizad/";
  static public Stage rootStage;
  public static final ThemeManager themeManager = new ThemeManager();
  static Object controller;

  static public FXMLLoader load(String resourcePath) {
    return new FXMLLoader(Objects.requireNonNull(Main.class.getResource(baseResourcePath + "fxml/" + resourcePath)));
  }

  static public Optional<Parent> loadParent(String resourcePath) {
    try {
      FXMLLoader l = load(resourcePath);
      Parent parent = l.load();
      controller = l.getController();
      return Optional.ofNullable(parent);
    } catch (IOException e) {
      logger.error("Error loading resource: " + resourcePath, e);
      return Optional.empty();
    }
  }

  static void cleanUpLastScene() {
    if (controller instanceof CleanableScene sc) {
      sc.cleanup();
    }
  }

  static public void navigate(String resourcePath, String title) {
    cleanUpLastScene();
    Optional<Parent> window = loadParent(resourcePath);
    if (window.isEmpty()) {
      return;
    }
    Scene scene = new Scene(window.get());

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

    // add on close listener to clean up
    controller.setOnCloseRequest(e -> {
      if (controller instanceof CleanableScene sc) {
        sc.cleanup();
      }
    });
  }
}
