package com.r.projektnizad.main;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.PrimerDark;
import com.r.projektnizad.services.AppPropertiesService;
import com.r.projektnizad.services.AuthService;
import com.r.projektnizad.services.HistoryChangeService;
import com.r.projektnizad.util.Navigator;
import com.r.projektnizad.util.ThemeManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

public class Main extends Application {
  public static final Logger logger = LoggerFactory.getLogger(Main.class);
  public static AuthService authService;

  @Override
  public void start(Stage stage) {
    Navigator.rootStage = stage;
    //Navigator.navigate("login.fxml", "Hello!");
    Locale.setDefault(new Locale("hr", "HR"));
    Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
    Navigator.navigate("hello-view.fxml", "Prijava");
  }

  public static void main(String[] args) {
    AppPropertiesService.load();
    authService = new AuthService();
    launch();
  }
}
