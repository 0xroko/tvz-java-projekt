package com.r.projektnizad.controllers.order;

import atlantafx.base.controls.MaskTextField;
import atlantafx.base.theme.Styles;
import com.r.projektnizad.exceptions.DatabaseActionFailException;
import com.r.projektnizad.repositories.TableRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModifyOrderDialog extends Dialog<Order> {
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

    try {
      List<Table> tables = new TableRepository().getAll();
      tableComboBox.getItems().addAll(tables);
    } catch (DatabaseActionFailException e) {
      new AppDialog().showExceptionMessage(e);
    }

    Util.comboBoxCellFactorySetter(tableComboBox, Table::getName);

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

  public ModifyOrderDialog(Optional<Order> order) {
    Navigator.asDialog("order/add.fxml", this);

    boolean isEdit = order.isPresent();

    // if is edit and order status is done, disable the nowCheckBox and the date picker
    if (isEdit && order.get().getStatus() == OrderStatus.IN_PROGRESS) {
      nowCheckBox.setDisable(true);
      datePicker.setDisable(true);
      timeTextField.setDisable(true);
    }

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

      if (param == CustomButtonTypes.EDIT && order.isPresent()) {
        ButtonType buttonType = new AppDialog().showConfirmationMessage("Izmjena narudžbe", "Jeste li sigurni da želite izmijeniti narudžbu?", CustomButtonTypes.EDIT);
        if (buttonType == CustomButtonTypes.CANCEL) {
          return null;
        }

        return new Order(
                order.get().getId(),
                order.get().getItemsOnOrder(),
                tableComboBox.getSelectionModel().getSelectedItem(),
                order.get().getStatus(),
                order.get().getUserId(),
                order.get().getOrderTime(),
                detailsTextField.getText()
        );
      }

      return new Order(
              null,
              new ArrayList<>(),
              tableComboBox.getSelectionModel().getSelectedItem(),
              nowCheckBox.isSelected() ? OrderStatus.IN_PROGRESS : OrderStatus.RESERVED,
              Main.authService.getCurrentUser().get().getId(),
              LocalDateTime.of(datePicker.getValue(), Util.parseTime(timeTextField.getText())),
              detailsTextField.getText()
      );
    });
    getDialogPane().getButtonTypes().addAll(isEdit ? CustomButtonTypes.EDIT : CustomButtonTypes.ADD, CustomButtonTypes.CANCEL);
  }
}
