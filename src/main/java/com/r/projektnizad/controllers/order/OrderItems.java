/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.order;

import com.r.projektnizad.models.ItemOnOrder;
import com.r.projektnizad.util.CustomTableView;
import com.r.projektnizad.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;

public class OrderItems extends Dialog<Boolean> {

  @FXML
  Button addItemToOrderButton;
  @FXML
  CustomTableView<ItemOnOrder> orderItemsTableView;
  @FXML
  TableColumn<ItemOnOrder, String> itemNameTableColumn;
  @FXML
  TableColumn<ItemOnOrder, String> statusTableColumn;
  @FXML
  TableColumn<ItemOnOrder, String> priceTableColumn;
  @FXML
  TableColumn<ItemOnOrder, String> categoryTableColumn;

  public OrderItems(Long orderId) {
    Navigator.asDialog("order/items.fxml", this);
  }
}
