package com.r.projektnizad.controllers;

import com.r.projektnizad.main.Main;
import com.r.projektnizad.util.Navigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.util.Objects;

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
}
