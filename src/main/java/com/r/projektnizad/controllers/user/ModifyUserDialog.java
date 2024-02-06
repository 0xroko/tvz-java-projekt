/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.user;

import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import com.r.projektnizad.enums.UserType;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.User;
import com.r.projektnizad.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.Validator;

import java.util.Optional;

public class ModifyUserDialog extends Dialog<Boolean> {
  @FXML
  private TextField userNameTextField;
  @FXML
  private PasswordTextField passwordTextField;
  @FXML
  private PasswordTextField passwordAgainTextField;
  @FXML
  private ComboBox<UserType> roleComboBox;
  @FXML
  private VBox passwordSection;
  @FXML
  private Label titleLabel;

  final Validator validator = new Validator();

  @FXML
  private void initialize() {
    roleComboBox.getItems().setAll(UserType.getValues());
    roleComboBox.getSelectionModel().selectFirst();
    Util.comboBoxCellFactorySetter(roleComboBox, UserType::getName);

    validator.createCheck()
            .dependsOn("userNameTextField", userNameTextField.textProperty())
            .withMethod(c -> {
              if (userNameTextField.getText().isEmpty()) {
                c.error("Korisničko ime je obavezno");
                userNameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
              }
              if (Main.authService.getUsers().stream().anyMatch(u -> u.getUsername().equals(userNameTextField.getText()))) {
                c.error("Korisnik već postoji");
                userNameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
              }
              userNameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            }).decorates(userNameTextField).immediate();

    validator.validationResultProperty().addListener((observable, oldValue, newValue) -> {
      Button addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.ADD);
      if (addButton == null) addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.EDIT);
      addButton.setDisable(!newValue.getMessages().isEmpty());
    });

  }

  public ModifyUserDialog(Optional<User> user) {
    Navigator.asDialog("user/add.fxml", this);
    boolean isEdit = user.isPresent();

    if (isEdit) {
      setTitle("Izmjeni korisnika");
      titleLabel.setText("Izmjeni korisnika");
      passwordSection.setVisible(false);
      userNameTextField.setText(user.get().getUsername());
      roleComboBox.setValue(user.get().getUserType());
    } else {
      validator.createCheck()
              .dependsOn("passwordTextField", passwordTextField.textProperty())
              .withMethod(c -> {
                if (passwordTextField.getPassword().isEmpty()) {
                  c.error("Lozinka je obavezna");
                  passwordAgainTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                  return;
                }
                if (passwordTextField.getPassword().length() < Config.PASSWORD_MIN_LENGTH) {
                  c.error("Lozinka je prekratka");
                  passwordAgainTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                  return;
                }
                passwordAgainTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
              }).decorates(passwordTextField).immediate();

      validator.createCheck()
              .dependsOn("passwordAgainTextField", passwordAgainTextField.textProperty())
              .dependsOn("passwordTextField", passwordTextField.textProperty())
              .withMethod(c -> {
                if (!passwordAgainTextField.getPassword().equals(passwordTextField.getPassword())) {
                  c.error("Lozinke se ne podudaraju");
                  passwordAgainTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                  return;
                }
                passwordAgainTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
              }).decorates(passwordAgainTextField).immediate();
    }


    setResultConverter(buttonType -> {
      if (buttonType == CustomButtonTypes.CANCEL) {
        return null;
      }
      if (buttonType == CustomButtonTypes.EDIT || buttonType == CustomButtonTypes.ADD) {
        if (isEdit) {
          ButtonType type = new AppDialog().showConfirmationMessage("Izmjena korisnika", "Jeste li sigurni da želite izmjeniti korisnika?", CustomButtonTypes.EDIT);
          if (type != CustomButtonTypes.EDIT) {
            return null;
          }
          User u = user.get().clone();
          u.setUsername(userNameTextField.getText());
          u.setUserType(roleComboBox.getValue());
          if (!passwordTextField.getPassword().isEmpty()) {
            u.setPassword(User.hashPassword(passwordTextField.getPassword()).get());
          }
          Main.authService.editUser(u);
        } else {
          Main.authService.register(userNameTextField.getText(), passwordTextField.getPassword(), roleComboBox.getValue());
        }
      }
      return true;
    });
    getDialogPane().getButtonTypes().addAll(isEdit ? CustomButtonTypes.EDIT : CustomButtonTypes.ADD, CustomButtonTypes.CANCEL);
  }
}
