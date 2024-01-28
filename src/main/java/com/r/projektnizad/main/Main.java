package com.r.projektnizad.main;

import com.r.projektnizad.services.AppPropertiesService;
import com.r.projektnizad.services.AuthService;
import com.r.projektnizad.util.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main extends Application {
  public static final Logger logger = LoggerFactory.getLogger(Main.class);
  public static AuthService authService;

  @Override
  public void start(Stage stage) throws IOException {
    Navigator.rootStage = stage;
    //Navigator.navigate("login.fxml", "Hello!");
    Navigator.navigate("hello-view.fxml", "Prijava");
  }

  public static void main(String[] args) {
    AppPropertiesService.load();
    authService = new AuthService();
    launch();
  }
}
