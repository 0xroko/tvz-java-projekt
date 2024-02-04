/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.user;

import atlantafx.base.controls.PasswordTextField;
import com.r.projektnizad.enums.UserType;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.User;
import com.r.projektnizad.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.Validator;

import java.util.Optional;

public class AddDialog extends Dialog<Boolean> {
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


  Validator validator = new Validator();

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
              }
            }).decorates(userNameTextField).immediate();

    validator.validationResultProperty().addListener((observable, oldValue, newValue) -> {
      Button addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.ADD);
      if (addButton == null) addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.EDIT);
      addButton.setDisable(!newValue.getMessages().isEmpty());
    });

  }

  public AddDialog(Optional<User> user) {
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
                if (passwordTextField.getText().isEmpty()) {
                  c.error("Lozinka je obavezna");
                }
                if (passwordTextField.getText().length() < Config.PASSWORD_MIN_LENGTH) {
                  c.error("Lozinka je prekratka");
                }
              }).decorates(passwordTextField).immediate();

      validator.createCheck()
              .dependsOn("passwordAgainTextField", passwordAgainTextField.textProperty())
              .dependsOn("passwordTextField", passwordTextField.textProperty())
              .withMethod(c -> {
                if (!passwordAgainTextField.getText().equals(passwordTextField.getText())) {
                  c.error("Lozinke se ne podudaraju");
                }
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
          User u = user.get();
          u.setUsername(userNameTextField.getText());
          u.setUserType(roleComboBox.getValue());
          if (!passwordTextField.getText().isEmpty()) {
            u.setPassword(User.hashPassword(passwordTextField.getText()).get());
          }
          Main.authService.editUser(u);
        } else {
          Main.authService.register(userNameTextField.getText(), passwordTextField.getText(), roleComboBox.getValue());
        }
      }
      return null;
    });
    getDialogPane().getButtonTypes().addAll(isEdit ? CustomButtonTypes.EDIT : CustomButtonTypes.ADD, CustomButtonTypes.CANCEL);
  }
}
