package com.r.projektnizad.controllers;

import atlantafx.base.theme.Styles;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.util.AppDialog;
import com.r.projektnizad.util.Config;
import com.r.projektnizad.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.synedra.validatorfx.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login {
  private final Logger logger = LoggerFactory.getLogger(Login.class);
  private final Validator validator = new Validator();
  public TextField usernameInput;
  public PasswordField passwordInput;

  @FXML
  void initialize() {

    validator.createCheck()
            .dependsOn("username", usernameInput.textProperty())
            .withMethod(c -> {
              String userName = c.get("username");
              if (userName.length() < 3) {
                usernameInput.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                c.error("Korisničko ime mora imati najmanje 3 znaka.");
              } else {
                usernameInput.pseudoClassStateChanged(Styles.STATE_DANGER, false);
              }
            })
            .decorates(usernameInput);

    validator.createCheck()
            .dependsOn("password", passwordInput.textProperty())
            .withMethod(c -> {
              String password = c.get("password");
              if (password.length() < Config.PASSWORD_MIN_LENGTH) {
                c.error("Lozinka prekratka.");
                passwordInput.pseudoClassStateChanged(Styles.STATE_DANGER, true);
              } else {
                passwordInput.pseudoClassStateChanged(Styles.STATE_DANGER, false);
              }
            })
            .decorates(passwordInput);
  }

  @FXML
  void login(ActionEvent actionEvent) {
    boolean valid = validator.validate();
    if (!valid) {
      return;
    }

    // TODO ERRORS
    boolean loggedIn = Main.authService.authenticate(usernameInput.getText(), passwordInput.getText());
    if (loggedIn) {
      logger.info("User " + usernameInput.getText() + " logged in.");
      Navigator.navigate("hello-view.fxml", "Pozdrav");
    } else {
      new AppDialog().showErrorMessage("Greška", "Pogrešno korisničko ime ili lozinka.");
      logger.info("User " + usernameInput.getText() + " failed to log in.");
    }
  }
}
