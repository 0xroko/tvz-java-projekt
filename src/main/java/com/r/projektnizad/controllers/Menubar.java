package com.r.projektnizad.controllers;

import com.r.projektnizad.enums.UserType;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class Menubar {
  public MenuBar menubar;
  public Menu logoutMenu;
  public Menu itemMenu;
  public Menu changesMenu;
  public MenuItem usersMenuItem;
  public MenuBar usersMenuBar;
  public Menu tableMenu;

  @FXML
  void initialize() {
    String username = Main.authService.getCurrentUser().get().getUsernameWithType();
    logoutMenu.setText(username);

    // remove changes, items and users from menu if user is not admin
    if (!Main.authService.getCurrentUser().get().getUserType().equals(UserType.ADMIN)) {
      menubar.getMenus().remove(changesMenu);
      menubar.getMenus().remove(itemMenu);
      menubar.getMenus().remove(tableMenu);
      usersMenuBar.getMenus().forEach(menu -> menu.getItems().remove(usersMenuItem));
    }
  }

  public void viewOrders(ActionEvent actionEvent) {
    Navigator.navigate("order/view.fxml", "Narudžbe");
  }

  public void addOrder(ActionEvent actionEvent) {
  }

  public void viewItems(ActionEvent actionEvent) {
    Navigator.navigate("item/view.fxml", "Artikli");
  }

  public void addItem(ActionEvent actionEvent) {
  }

  public void addReservation(ActionEvent actionEvent) {
  }

  public void viewReservations(ActionEvent actionEvent) {
  }

  public void viewCategories(ActionEvent actionEvent) {
    Navigator.navigate("category/view.fxml", "Kategorije");
  }


  public void viewHistoryChanges(ActionEvent actionEvent) {
    Navigator.navigate("history.fxml", "Povijest Promjena");
  }

  public void logout(ActionEvent actionEvent) {
    Main.authService.logout();
    Navigator.navigate("login.fxml", "Prijava");
  }

  public void viewUsers(ActionEvent actionEvent) {
    Navigator.navigate("user/view.fxml", "Korisnici");
  }

  public void viewTables(ActionEvent actionEvent) {
    Navigator.navigate("table/view.fxml", "Stolovi");
  }
}
