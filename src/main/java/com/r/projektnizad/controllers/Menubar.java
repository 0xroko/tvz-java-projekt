package com.r.projektnizad.controllers;

import com.r.projektnizad.main.Main;
import com.r.projektnizad.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class Menubar {
  public MenuBar menubar;
  public Menu logoutMenu;

  @FXML
  void initialize() {
    String username = Main.authService.getCurrentUser().get().getUsernameWithType();
    logoutMenu.setText(username);
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
}
