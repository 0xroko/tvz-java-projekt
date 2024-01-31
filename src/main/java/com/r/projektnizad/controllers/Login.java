package com.r.projektnizad.controllers;

import com.r.projektnizad.main.Main;
import com.r.projektnizad.util.AppDialog;
import com.r.projektnizad.util.ControlClassSetter;
import com.r.projektnizad.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login {

  private final Logger logger = LoggerFactory.getLogger(Login.class);

  private final Validator validator = new Validator();

  public TextField usernameInput;
  public PasswordField passwordInput;

  private ControlClassSetter userNameErrorClassSetter;
  private ControlClassSetter passwordErrorClassSetter;

  @FXML
  void initialize() {
    userNameErrorClassSetter = new ControlClassSetter(usernameInput, "input-field-error");
    passwordErrorClassSetter = new ControlClassSetter(passwordInput, "input-field-error");

    validator.createCheck()
            .dependsOn("username", usernameInput.textProperty())
            .withMethod(c -> {
              String userName = c.get("username");
              if (userName.length() < 3) {
                userNameErrorClassSetter.set();
                c.error("Korisničko ime mora imati najmanje 3 znaka.");
              } else {
                userNameErrorClassSetter.unset();
              }
            })
            .decorates(usernameInput);

    validator.createCheck()
            .dependsOn("password", passwordInput.textProperty())
            .withMethod(c -> {
              String password = c.get("password");
              if (password.length() < 4) {
                c.error("Lozinka mora imati najmanje 4 znaka.");
                passwordErrorClassSetter.set();
              } else {
                passwordErrorClassSetter.unset();
              }
            })
            .decorates(passwordInput);
  }

  void clearErrors() {
    userNameErrorClassSetter.unset();
    passwordErrorClassSetter.unset();
    usernameInput.setText("");
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
      clearErrors();
      logger.info("User " + usernameInput.getText() + " failed to log in.");
    }
  }
}
