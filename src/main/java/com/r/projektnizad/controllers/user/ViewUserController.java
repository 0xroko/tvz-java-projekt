package com.r.projektnizad.controllers.user;

import com.r.projektnizad.enums.UserType;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.User;
import com.r.projektnizad.util.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ViewUserController {
  public CustomTableView<User> userTableView;
  public TableColumn<User, Long> idTableColumn;
  public TableColumn<User, String> nameTableColumn;
  public TableColumn<User, String> roleTableColumn;
  public TextField nameSearchTextField;
  public ComboBox<UserType> roleComboBox;

  public final ObservableList<User> users = FXCollections.observableArrayList();

  private void updateUserTable() {
    users.clear();
    users.addAll(Main.authService.getUsers());
  }

  @FXML
  public void initialize() {
    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
    nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
    roleTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserType().getName()));
    updateUserTable();
    roleComboBox.setItems(FXCollections.observableList(UserType.getFilterableValues()));
    roleComboBox.getSelectionModel().selectFirst();

    Util.comboBoxCellFactorySetter(roleComboBox, UserType::getName);

    FilteredList<User> filteredData = new FilteredList<>(users, p -> true);

    ObjectProperty<Predicate<User>> usernameFilter = new SimpleObjectProperty<>();
    ObjectProperty<Predicate<User>> roleFilter = new SimpleObjectProperty<>();

    usernameFilter.bind(Bindings.createObjectBinding(() ->
            user -> user.getUsername().toLowerCase().contains(nameSearchTextField.getText().toLowerCase()), nameSearchTextField.textProperty()));

    roleFilter.bind(Bindings.createObjectBinding(() ->
            user -> roleComboBox.getValue() == UserType.ALL || user.getUserType().equals(roleComboBox.getValue()), roleComboBox.valueProperty()));

    filteredData.predicateProperty().bind(Bindings.createObjectBinding(
            () -> usernameFilter.get().and(roleFilter.get()), usernameFilter, roleFilter));

    userTableView.setItems(filteredData);

    Map<String, Consumer<User>> actions = getContextMenuActions();

    userTableView.setRowFactory(tv -> {
      var row = new TableRow<User>();
      TableViewContextMenu.build(row, actions);
      return row;
    });
  }

  private Map<String, Consumer<User>> getContextMenuActions() {
    Map<String, Consumer<User>> actions = new LinkedHashMap<>();

    actions.put("Izmijeni", user -> new ModifyUserDialog(Optional.of(user)).showAndWait().ifPresent(u -> updateUserTable()));
    actions.put("Obriši", user -> {
      ButtonType result = new AppDialog().showConfirmationMessage("Brisanje korisnika", "Da li ste sigurni da želite obrisati korisnika " + user.getUsername() + "?", CustomButtonTypes.DELETE);
      if (result == CustomButtonTypes.DELETE) {
        Main.authService.deleteUser(user);
        updateUserTable();
      }
    });

    // change password
    actions.put("Promijeni lozinku", user -> new ChangePasswordDialog(user.getId()).showAndWait().ifPresent(u -> updateUserTable()));
    return actions;
  }

  public void openAddUser(ActionEvent actionEvent) {
    new ModifyUserDialog(Optional.empty()).showAndWait().ifPresent(user -> updateUserTable());
  }
}
