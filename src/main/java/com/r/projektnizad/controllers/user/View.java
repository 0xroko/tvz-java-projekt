package com.r.projektnizad.controllers.user;

import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.User;
import com.r.projektnizad.util.AppDialog;
import com.r.projektnizad.util.CustomButtonTypes;
import com.r.projektnizad.util.CustomTableView;
import com.r.projektnizad.util.TableViewContextMenu;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class View {
  public CustomTableView<User> userTableView;
  public TableColumn<User, Long> idTableColumn;
  public TableColumn<User, String> nameTableColumn;
  public TableColumn<User, String> roleTableColumn;

  private void updateUserTable() {
    userTableView.setItems(Main.authService.getUsers().stream().toList());
  }

  @FXML
  public void initialize() {
    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
    nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
    roleTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserType().getName()));
    updateUserTable();

    Map<String, Consumer<User>> actions = new LinkedHashMap<>();

    actions.put("Izmijeni", user -> {
      new AddDialog(Optional.of(user)).showAndWait().ifPresent(u -> {

      });
    });
    actions.put("Obriši", user -> {
      ButtonType result = new AppDialog().showConfirmationMessage("Brisanje korisnika", "Da li ste sigurni da želite obrisati korisnika " + user.getUsername() + "?", CustomButtonTypes.DELETE);
      if (result == CustomButtonTypes.DELETE) {
        Main.authService.deleteUser(user);
        updateUserTable();
      }
    });

    // change password
    actions.put("Promijeni lozinku", user -> {
      new ChangePasswordDialog(user.getId()).showAndWait().ifPresent(u -> updateUserTable());
    });

    userTableView.setRowFactory(tv -> {
      var row = new TableRow<User>();
      TableViewContextMenu.build(row, actions);
      return row;
    });
  }

  public void openAddUser(ActionEvent actionEvent) {
    new AddDialog(Optional.empty()).showAndWait().ifPresent(user -> updateUserTable());
  }
}
