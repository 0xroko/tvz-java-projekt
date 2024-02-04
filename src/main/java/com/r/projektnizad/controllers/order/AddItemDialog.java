/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.order;

import atlantafx.base.theme.Styles;
import com.r.projektnizad.enums.ItemOnOrderStatus;
import com.r.projektnizad.models.Item;
import com.r.projektnizad.models.ItemOnOrder;
import com.r.projektnizad.repositories.ItemRepository;
import com.r.projektnizad.util.*;
import com.r.projektnizad.util.controlfx.SearchableComboBox;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import net.synedra.validatorfx.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddItemDialog extends Dialog<List<ItemOnOrder>> {
  @FXML
  private Spinner<Integer> stockSpinner;
  @FXML
  private VBox itemComboBoxVBox;
  @FXML
  private Label amountErrorLabel;
  @FXML
  private Label stockAmountLabel;
  @FXML
  SearchableComboBox<Item> itemComboBox;

  Validator validator = new Validator();

  Optional<Item> item = Optional.empty();

  @FXML
  void initialize() {
    ItemRepository itemRepository = new ItemRepository();
    List<Item> items = itemRepository.getAll();

    itemComboBox.setItems(FXCollections.observableArrayList(items));
    itemComboBox.getSelectionModel().selectFirst();

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

    stockSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1, 1));
    stockSpinner.setEditable(true);
    stockSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
      validator.validate();
    });

    validator.createCheck()
            .dependsOn("amountTextField", stockSpinner.valueProperty())
            .withMethod(c -> {
              if (item.isPresent()) {
                try {
                  if (stockSpinner.getValue() > item.get().getStock()) {
                    c.error("Nema dovoljno (dostupno: " + item.get().getStock() + ")");
                    stockSpinner.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                    return;
                  }
                } catch (NumberFormatException e) {
                  // silently ignore since validator will take care of it
                }
              }
              stockSpinner.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            }).decorates(stockSpinner)
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

  public AddItemDialog() {
    Navigator.asDialog("order/add-item.fxml", this);

    setResultConverter(buttonType -> {
      if (buttonType == CustomButtonTypes.ADD) {
        ItemOnOrder i = new ItemOnOrder(null, itemComboBox.getValue().clone(), LocalDateTime.now(), ItemOnOrderStatus.DONE);
        // create list of n items i
        List<ItemOnOrder> items = new ArrayList<>();
        for (int j = 0; j < stockSpinner.getValue(); j++) {
          items.add(i.clone());
        }
        return items;
      }
      return null;
    });

    getDialogPane().getButtonTypes().addAll(CustomButtonTypes.ADD, CustomButtonTypes.CANCEL);
  }
}
