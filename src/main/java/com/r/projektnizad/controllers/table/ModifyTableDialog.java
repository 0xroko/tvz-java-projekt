/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.table;

import com.r.projektnizad.models.Table;
import com.r.projektnizad.util.CustomButtonTypes;
import com.r.projektnizad.util.Navigator;
import com.r.projektnizad.util.Validators;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.synedra.validatorfx.Validator;

import java.util.Optional;

public class ModifyTableDialog extends Dialog<Table> {
  @FXML
  private Label titleLabel;
  @FXML
  private TextField nameTextField;
  @FXML
  private TextField descriptionTextField;
  @FXML
  private TextField seatsTextField;
  private final Validator validator = new Validator();

  @FXML
  private void initialize() {
    Validators.buildTextFieldValidator(seatsTextField, Validators.number(3, 0));

    validator.createCheck().dependsOn("seatsTextField", seatsTextField.textProperty()).withMethod(c -> {
      if (!seatsTextField.getText().isEmpty()) {
      } else {
        c.error("Broj sjedala mora biti cijeli broj");
      }
    }).decorates(seatsTextField).immediate();

    validator.createCheck().dependsOn("nameTextField", nameTextField.textProperty()).withMethod(c -> {
      if (!nameTextField.getText().isEmpty()) {
      } else {
        c.error("Ime stola ne smije biti prazno");
      }
    }).decorates(nameTextField).immediate();

    validator.createCheck().dependsOn("descriptionTextField", descriptionTextField.textProperty()).withMethod(c -> {
      if (!descriptionTextField.getText().isEmpty()) {
      } else {
        c.error("Opis stola ne smije biti prazan");
      }
    }).decorates(descriptionTextField).immediate();

    validator.validationResultProperty().addListener((observable, oldValue, newValue) -> {
      Button addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.ADD);
      if (addButton == null) addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.EDIT);
      addButton.setDisable(!newValue.getMessages().isEmpty());
    });

  }

  public ModifyTableDialog(Optional<Table> table) {
    Navigator.asDialog("table/modify-dialog.fxml", this);

    boolean isEdit = table.isPresent();

    if (isEdit) {
      titleLabel.setText("Uredi stol");
      nameTextField.setText(table.get().getName());
      descriptionTextField.setText(table.get().getDescription());
      seatsTextField.setText(String.valueOf(table.get().getSeats()));
    } else {
      seatsTextField.setText("0");
    }


    setResultConverter(buttonType -> {
      if (buttonType == CustomButtonTypes.ADD) {
        return new Table(
                null,
                nameTextField.getText(),
                descriptionTextField.getText(),
                Long.parseLong(seatsTextField.getText())
        );
      }

      if (buttonType == CustomButtonTypes.EDIT) {
        Table t = table.get();
        t.setName(nameTextField.getText());
        t.setDescription(descriptionTextField.getText());
        t.setSeats(Long.parseLong(seatsTextField.getText()));
        return t;
      }

      return null;
    });

    getDialogPane().getButtonTypes().addAll(isEdit ? CustomButtonTypes.EDIT : CustomButtonTypes.ADD, CustomButtonTypes.CANCEL);

  }
}
