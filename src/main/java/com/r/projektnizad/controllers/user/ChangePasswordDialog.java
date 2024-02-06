/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.user;

import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.util.AppDialog;
import com.r.projektnizad.util.CustomButtonTypes;
import com.r.projektnizad.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import net.synedra.validatorfx.Validator;

import static com.r.projektnizad.util.Config.PASSWORD_MIN_LENGTH;

public class ChangePasswordDialog extends Dialog<Boolean> {
  @FXML
  private PasswordTextField passwordTextField;
  @FXML
  private PasswordTextField passwordAgainTextField;
  final Validator validator = new Validator();

  @FXML
  private void initialize() {
    validator.createCheck()
            .dependsOn("passwordTextField", passwordTextField.textProperty())
            .withMethod(c -> {
              if (passwordTextField.getPassword().length() < PASSWORD_MIN_LENGTH) {
                c.error("Lozinka je prekratka");
                passwordTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
              }
              if (passwordTextField.getPassword().isEmpty()) {
                c.error("Lozinka je obavezna");
                passwordTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
              }
              passwordTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            }).decorates(passwordTextField).immediate();
    validator.createCheck()
            .dependsOn("passwordAgainTextField", passwordAgainTextField.textProperty())
            .dependsOn("passwordTextField", passwordTextField.textProperty())
            .withMethod(c -> {
              if (!passwordAgainTextField.getPassword().equals(passwordTextField.getPassword())) {
                passwordAgainTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                c.error("Lozinke se ne podudaraju");
                return;
              }
              passwordAgainTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            }).decorates(passwordAgainTextField).immediate();

    validator.validationResultProperty().addListener((observable, oldValue, newValue) -> {
      var addButton = getDialogPane().lookupButton(CustomButtonTypes.EDIT);
      addButton.setDisable(!newValue.getMessages().isEmpty());
    });
  }

  public ChangePasswordDialog(Long userId) {
    Navigator.asDialog("user/change-password.fxml", this);

    setResultConverter(buttonType -> {
      if (buttonType == CustomButtonTypes.EDIT) {
        boolean updated = Main.authService.updatePassword(userId, passwordTextField.getPassword());
        if (updated) {
          return true;
        }
        new AppDialog().showErrorMessage("Greška", "Greška prilikom promjene lozinke");
      }
      return false;
    });

    getDialogPane().getButtonTypes().addAll(CustomButtonTypes.EDIT, CustomButtonTypes.CANCEL);
  }
}
