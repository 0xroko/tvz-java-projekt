package com.r.projektnizad.controllers;

import com.r.projektnizad.main.Main;
import com.r.projektnizad.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class Menubar {
  public MenuBar menubar;
  public HBox leading;
  public Label userNameLabel;
  public ImageView logout;

  @FXML
  void initialize() {

    /*add loged username and logout button */
    String username = Main.authService.getCurrentUser().get().getUsername();
    userNameLabel.setText(username);

    /*logout */
    logout.setOnMouseClicked(event -> {
      Main.authService.logout();
      Navigator.navigate("login.fxml", "Prijava");
    });

  }

  public void viewOrders(ActionEvent actionEvent) {
  }

  public void addOrder(ActionEvent actionEvent) {
  }

  public void viewItems(ActionEvent actionEvent) {
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

  public void addCategory(ActionEvent actionEvent) {
    Navigator.navigate("category/add.fxml", "Dodaj kategoriju");
  }
}
