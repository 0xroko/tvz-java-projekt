package com.r.projektnizad.controllers.item;

import atlantafx.base.controls.MaskTextField;
import atlantafx.base.theme.Styles;
import com.r.projektnizad.exceptions.DatabaseActionFailException;
import com.r.projektnizad.repositories.CategoryRepository;
import com.r.projektnizad.enums.ItemType;
import com.r.projektnizad.models.Category;
import com.r.projektnizad.models.Item;
import com.r.projektnizad.models.ItemBuilder;
import com.r.projektnizad.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.Validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ModifyItemDialog extends Dialog<Item> {
  @FXML
  TextField nameTextField;
  @FXML
  TextField descriptionTextField;
  @FXML
  TextField priceTextField;
  @FXML
  TextField stockTextField;
  @FXML
  TextField defaultStockIncrementTextField;
  @FXML
  MaskTextField preparationTimeTextField;
  @FXML
  ComboBox<ItemType> itemTypeComboBox;
  @FXML
  ComboBox<Category> categoryComboBox;
  @FXML
  VBox preparationField;

  final CategoryRepository categoryRepository = new CategoryRepository();

  @FXML
  void initialize() {
    preparationTimeTextField.setMask("99:99");

    itemTypeComboBox.getItems().addAll(ItemType.values());
    Util.comboBoxCellFactorySetter(itemTypeComboBox, ItemType::getName);

    try {
      List<Category> categories = categoryRepository.getAll();
      categoryComboBox.getItems().addAll(categories);

    } catch (DatabaseActionFailException e) {
      new AppDialog().showExceptionMessage(e);
    }

    categoryComboBox.setCellFactory(param -> Util.getComboBoxListCell(Category::getName));
    categoryComboBox.setButtonCell(Util.getComboBoxListCell(Category::getName));

    Validators.buildTextFieldValidator(priceTextField, Validators.number(5, 2));
    Validators.buildTextFieldValidator(defaultStockIncrementTextField, Validators.number(3, 0));
    Validators.buildTextFieldValidator(stockTextField, Validators.number(2, 0));

    // only allow preparation time to be visible if the item is a PREPARABLE
    itemTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> preparationField.setVisible(newValue == ItemType.PREPARABLE));

    validator.createCheck()
            .dependsOn("name", nameTextField.textProperty())
            .withMethod(c -> {
              String name = c.get("name");
              if (name.length() < 3) {
                c.error("Naziv mora imati najmanje 3 znaka.");
                nameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
              }
              nameTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            })
            .decorates(nameTextField).immediate();

    validator.createCheck()
            .dependsOn("description", descriptionTextField.textProperty())
            .withMethod(c -> {
              String description = c.get("description");
              if (description.length() < 3) {
                c.error("Opis mora imati najmanje 3 znaka.");
                descriptionTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
              }
              descriptionTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            })
            .decorates(descriptionTextField).immediate();

    validator.createCheck()
            .dependsOn("price", priceTextField.textProperty())
            .withMethod(c -> {
              String price = c.get("price");
              if (price.isEmpty()) {
                c.error("Cijena mora biti postavljena.");
                priceTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
              }
              priceTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            })
            .decorates(priceTextField).immediate();

    validator.createCheck()
            .dependsOn("stock", stockTextField.textProperty())
            .withMethod(c -> {
              String stock = c.get("stock");
              if (stock.isEmpty()) {
                c.error("Zalihe moraju biti postavljene.");
                stockTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
              }
              stockTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            })
            .decorates(stockTextField).immediate();

    validator.createCheck()
            .dependsOn("defaultStockIncrement", defaultStockIncrementTextField.textProperty())
            .withMethod(c -> {
              String defaultStockIncrement = c.get("defaultStockIncrement");
              if (defaultStockIncrement.isEmpty()) {
                c.error("Zadani broj za dopunu zaliha mora biti postavljen.");
                defaultStockIncrementTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
                return;
              }
              defaultStockIncrementTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
            })
            .decorates(defaultStockIncrementTextField).immediate();
  }

  private final Validator validator = new Validator();

  public ModifyItemDialog(Optional<Item> editItem) {
    Navigator.asDialog("item/add.fxml", this);

    boolean isEdit = editItem.isPresent();

    if (isEdit) {
      Item item = editItem.get();
      nameTextField.setText(item.getName());
      descriptionTextField.setText(item.getDescription());
      priceTextField.setText(item.getPrice().toString());
      stockTextField.setText(item.getStock().toString());
      defaultStockIncrementTextField.setText(item.getDefaultStockIncrement().toString());
      preparationTimeTextField.setText(Util.formatDuration(item.getPreparationTime()));
      itemTypeComboBox.getSelectionModel().select(item.getItemType());

      // since classes are not equal, we need to find the category with the same id
      // and select it
      categoryComboBox.getItems().stream().filter(category -> item.getCategory().getId().equals(category.getId())).findFirst().ifPresent(category -> categoryComboBox.getSelectionModel().select(category));
    } else {
      preparationTimeTextField.setText("00:00");
      categoryComboBox.getSelectionModel().selectFirst();
      itemTypeComboBox.getSelectionModel().selectFirst();
    }

    getDialogPane().getButtonTypes().addAll(isEdit ? CustomButtonTypes.EDIT : CustomButtonTypes.ADD, CustomButtonTypes.CANCEL);

    validator.validationResultProperty().addListener((observable, oldValue, newValue) -> {
      Button addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.ADD);
      if (addButton == null) addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.EDIT);

      addButton.setDisable(!newValue.getMessages().isEmpty());
    });

    setResultConverter(dialogButton -> {
      if (dialogButton == CustomButtonTypes.CANCEL) {
        return null;
      }

      ItemBuilder builder = new ItemBuilder().setName(nameTextField.getText())
              .setDescription(descriptionTextField.getText())
              .setPrice(new BigDecimal(priceTextField.getText()))
              .setStock(Long.valueOf(stockTextField.getText()))
              .setDefaultStockIncrement(Long.valueOf(defaultStockIncrementTextField.getText()))
              .setItemType(itemTypeComboBox.getSelectionModel().getSelectedItem())
              .setCategory(categoryComboBox.getSelectionModel().getSelectedItem());

      if (itemTypeComboBox.getSelectionModel().getSelectedItem() == ItemType.PREPARABLE) {
        builder.setPreparationTime(Util.parseDuration(preparationTimeTextField.getText()));
      }

      if (dialogButton == CustomButtonTypes.ADD) {
        return builder.createItem();
      }

      if (dialogButton == CustomButtonTypes.EDIT && isEdit) {
        ButtonType confirm = new AppDialog().showConfirmationMessage("Uredi artikl", "Da li ste sigurni da želite urediti artikl?", CustomButtonTypes.EDIT);
        if (confirm == CustomButtonTypes.CANCEL) return null;
        return builder.setId(editItem.get().getId()).createItem();
      }
      return null;
    });
  }


}
