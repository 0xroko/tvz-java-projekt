package com.r.projektnizad.controllers.order;

import atlantafx.base.controls.MaskTextField;
import atlantafx.base.theme.Styles;
import com.r.projektnizad.dao.TableRepository;
import com.r.projektnizad.enums.OrderStatus;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.Order;
import com.r.projektnizad.models.Table;
import com.r.projektnizad.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.synedra.validatorfx.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class AddDialog extends Dialog<Order> {
  @FXML
  ComboBox<Table> tableComboBox;
  @FXML
  TextField detailsTextField;
  @FXML
  DatePicker datePicker;
  @FXML
  MaskTextField timeTextField;
  @FXML
  CheckBox nowCheckBox;
  @FXML
  Label titleLabel;
  @FXML
  VBox dateTimeField;

  Validator validator = new Validator();

  @FXML
  void initialize() {
    timeTextField.setMask("99:99");

    datePicker.setValue(LocalDate.now());
    datePicker.getEditor().setDisable(true);
    datePicker.getEditor().setOpacity(1);

    tableComboBox.getItems().addAll(new TableRepository().getAll());
    Util.comboBoxCellFactorySetters(tableComboBox, Table::getName);

    dateTimeField.visibleProperty().bind(nowCheckBox.selectedProperty().not());
    // revalidate time field when nowCheckBox is selected
    nowCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        timeTextField.setText("00:00");
      }
    });

    validator.createCheck().dependsOn("time", timeTextField.textProperty()).withMethod(c -> {
      String time = timeTextField.getText();
      if (nowCheckBox.isSelected() || time.matches(Validators.time)) {
        timeTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
      } else {
        c.error("Vrijeme nije ispravno");
        timeTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
      }
    }).decorates(timeTextField).immediate();


    validator.createCheck().dependsOn("details", detailsTextField.textProperty()).withMethod(c -> {
      if (detailsTextField.getText().isEmpty()) {
        c.error("Morate unijeti detalje narudžbe");
        detailsTextField.pseudoClassStateChanged(Styles.STATE_DANGER, true);
        return;
      }
      detailsTextField.pseudoClassStateChanged(Styles.STATE_DANGER, false);
    }).decorates(detailsTextField).immediate();

  }

  public AddDialog(Optional<Order> order) {
    Navigator.asDialog("order/add.fxml", this);

    boolean isEdit = order.isPresent();

    if (isEdit) {
      titleLabel.setText("Izmjena narudžbe");
      Order o = order.get();
      datePicker.setValue(o.getOrderTime().toLocalDate());
      timeTextField.setText(Util.formatTime(o.getOrderTime()));
      detailsTextField.setText(o.getNote());
      tableComboBox.getSelectionModel().select(tableComboBox.getItems().stream().filter(t -> t.getId().equals(o.getTable().getId())).findFirst().orElse(null));
    } else {
      datePicker.setValue(LocalDate.now());
      timeTextField.setText("00:00");
      tableComboBox.getSelectionModel().selectFirst();
    }

    validator.validationResultProperty().addListener((observable, oldValue, newValue) -> {
      Button addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.ADD);
      if (addButton == null) addButton = (Button) getDialogPane().lookupButton(CustomButtonTypes.EDIT);
      addButton.setDisable(!newValue.getMessages().isEmpty());
    });


    setResultConverter(param -> {
      if (param == CustomButtonTypes.CANCEL) {
        return null;
      }

      if (param == CustomButtonTypes.EDIT) {
        ButtonType buttonType = new AppDialog().showConfirmationMessage("Izmjena narudžbe", "Jeste li sigurni da želite izmijeniti narudžbu?", CustomButtonTypes.EDIT);
        if (buttonType != CustomButtonTypes.EDIT) {
          return null;
        }
      }

      return new Order(
              isEdit ? order.get().getId() : null,
              new ArrayList<>(),
              tableComboBox.getSelectionModel().getSelectedItem(),
              nowCheckBox.isSelected() ? OrderStatus.IN_PROGRESS : OrderStatus.RESERVED,
              Main.authService.getCurrentUser().get().getId(),
              datePicker.getValue().atTime(Util.parseTime(timeTextField.getText()).toLocalTime()),
              detailsTextField.getText()
      );
    });
    getDialogPane().getButtonTypes().addAll(isEdit ? CustomButtonTypes.EDIT : CustomButtonTypes.ADD, CustomButtonTypes.CANCEL);
  }
}