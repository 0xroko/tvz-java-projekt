/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.order;

import atlantafx.base.theme.Styles;
import com.r.projektnizad.enums.ItemOnOrderStatus;
import com.r.projektnizad.exceptions.DatabaseActionFailException;
import com.r.projektnizad.models.Item;
import com.r.projektnizad.models.ItemOnOrder;
import com.r.projektnizad.repositories.ItemRepository;
import com.r.projektnizad.util.*;
import com.r.projektnizad.util.controlfx.SearchableComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import net.synedra.validatorfx.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModifyOrderItemsDialog extends Dialog<List<ItemOnOrder>> {
  @FXML
  private TextField stockTextField;
  @FXML
  private VBox itemComboBoxVBox;
  @FXML
  private Label amountErrorLabel;
  @FXML
  private Label stockAmountLabel;
  @FXML
  SearchableComboBox<Item> itemComboBox;

  final Validator validator = new Validator();

  Optional<Item> item = Optional.empty();

  @FXML
  void initialize() {
    try {
      ItemRepository itemRepository = new ItemRepository();
      List<Item> items = itemRepository.getAll();
      itemComboBox.getItems().addAll(items);
      itemComboBox.getSelectionModel().selectFirst();
    } catch (DatabaseActionFailException e) {
      new AppDialog().showExceptionMessage(e);
      setResult(null);
    }

    itemComboBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(Item object) {
        if (object == null) return "";
        return object.getName();
      }

      @Override
      public Item fromString(String string) {
        return null;
      }
    });

    stockTextField.setText("1");
    Validators.buildTextFieldValidator(stockTextField, Validators.number(3, 0));

    validator.createCheck()
            .dependsOn("amountTextField", stockTextField.textProperty())
            .withMethod(c -> {
              if (item.isPresent()) {
                if (stockTextField.getText().isEmpty()) {
                  c.error("Unesite količinu");
                  stockTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                  return;
                }
                try {
                  int amount = Integer.parseInt(stockTextField.getText());
                  if (amount > item.get().getStock()) {
                    c.error("Nema dovoljno (dostupno: " + item.get().getStock() + ")");
                    stockTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                    return;
                  }
                } catch (NumberFormatException e) {
                  // silently ignore since validator will take care of it
                }
              }
              stockTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            }).decorates(stockTextField)
            .immediate();

    amountErrorLabel.pseudoClassStateChanged(Styles.STATE_DANGER, true);
    amountErrorLabel.textProperty().bind(validator.createStringBinding());

    validator.createCheck()
            .dependsOn("itemComboBox", itemComboBox.valueProperty())
            .withMethod(c -> {
              if (itemComboBox.getValue() == null) {
                c.error("Odaberite artikl");
                itemComboBox.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
              }
              itemComboBox.pseudoClassStateChanged(Styles.STATE_DANGER, false);
              item = Optional.of(itemComboBox.getValue());
            }).decorates(itemComboBox)
            .immediate();

    validator.validationResultProperty().addListener((observable, oldValue, newValue) -> {
      Button addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.ADD);
      if (addButton == null) addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.EDIT);
      addButton.setDisable(!newValue.getMessages().isEmpty());
    });

  }

  public ModifyOrderItemsDialog() {
    Navigator.asDialog("order/add-item.fxml", this);

    setResultConverter(buttonType -> {
      if (buttonType == CustomButtonTypes.ADD) {
        ItemOnOrder i = new ItemOnOrder(null, itemComboBox.getValue().clone(), LocalDateTime.now(), ItemOnOrderStatus.DONE);
        // create list of n items i
        List<ItemOnOrder> items = new ArrayList<>();
        try {
          int amount = Integer.parseInt(stockTextField.getText());
          for (int j = 0; j < amount; j++) {
            items.add(i.clone());
          }
          return items;
        } catch (NumberFormatException ignored) {
        }

      }
      return null;
    });

    getDialogPane().getButtonTypes().addAll(CustomButtonTypes.ADD, CustomButtonTypes.CANCEL);
  }
}
