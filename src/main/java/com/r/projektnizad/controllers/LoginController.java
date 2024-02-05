package com.r.projektnizad.controllers;

import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import com.r.projektnizad.exceptions.InvalidPasswordException;
import com.r.projektnizad.exceptions.UserNotFoundException;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.util.AppDialog;
import com.r.projektnizad.util.Config;
import com.r.projektnizad.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import net.synedra.validatorfx.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.r.projektnizad.util.Config.STARTING_SCREEN;

public class LoginController {
  private final Logger logger = LoggerFactory.getLogger(LoginController.class);
  private final Validator validator = new Validator();
  public TextField usernameInput;
  public PasswordTextField passwordInput;

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

    try {
      Main.authService.authenticate(usernameInput.getText(), passwordInput.getPassword());
      Navigator.navigate(STARTING_SCREEN, "Narudžbe");
    } catch (InvalidPasswordException e) {
      new AppDialog().showErrorMessage("Greška", "Pogrešno lozinka.");
    } catch (UserNotFoundException e) {
      new AppDialog().showErrorMessage("Greška", "Korisnik nije pronađen.");
    }
  }
}
