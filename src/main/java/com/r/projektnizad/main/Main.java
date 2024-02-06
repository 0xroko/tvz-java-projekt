package com.r.projektnizad.main;

import atlantafx.base.theme.CupertinoDark;
import com.r.projektnizad.services.AppPropertiesService;
import com.r.projektnizad.services.AuthService;
import com.r.projektnizad.util.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Locale;

import static com.r.projektnizad.util.Config.STARTING_SCREEN;

public class Main extends Application {
  public static AuthService authService;

  @Override
  public void start(Stage stage) {
    Navigator.rootStage = stage;
    Locale.setDefault(Locale.forLanguageTag("hr"));
    Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());
    Navigator.navigate(STARTING_SCREEN, "Prijava");
  }

  public static void main(String[] args) {
    AppPropertiesService.load();
    authService = new AuthService();
    launch();
  }

}
