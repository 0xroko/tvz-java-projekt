/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.order;

import com.r.projektnizad.enums.ItemOnOrderStatus;
import com.r.projektnizad.models.CleanableScene;
import com.r.projektnizad.models.ItemOnOrder;
import com.r.projektnizad.models.ObservableThreadTask;
import com.r.projektnizad.models.Order;
import com.r.projektnizad.models.change.ModifyChange;
import com.r.projektnizad.repositories.OrderRepository;
import com.r.projektnizad.threads.ChangeWriterThread;
import com.r.projektnizad.threads.SignaledTaskThread;
import com.r.projektnizad.util.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.lang.constant.Constable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class OrderItems extends Dialog<Boolean> implements CleanableScene {
  @FXML
  Button addItemToOrderButton;
  @FXML
  CustomTableView<ItemOnOrder> itemOnOrderTableView;
  @FXML
  TableColumn<ItemOnOrder, String> itemNameTableColumn;
  @FXML
  TableColumn<ItemOnOrder, String> statusTableColumn;
  @FXML
  TableColumn<ItemOnOrder, String> priceTableColumn;
  @FXML
  TableColumn<ItemOnOrder, String> categoryTableColumn;

  Order order;
  OrderRepository orderRepository = new OrderRepository();
  SignaledTaskThread<List<ItemOnOrder>, Void> signaledTaskThread;

  @FXML
  void initialize() {
    addItemToOrderButton.setOnAction(event -> {
      addItemToOrder();
    });

    itemNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getName()));
    statusTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().getName()));
    priceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getPrice().toString()));
    categoryTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getCategory().getName()));

    Map<String, Consumer<ItemOnOrder>> actions = new LinkedHashMap<>();
    actions.put("Dodaj ponovno", this::addAgain);
    actions.put("Promijeni status", this::changeStatus);
    actions.put("Izbriši", this::deleteItem);

    itemOnOrderTableView.setRowFactory(tv -> {
      TableRow<ItemOnOrder> row = new TableRow<>();
      ContextMenu contextMenu = TableViewContextMenu.build(row, actions);
      // if done, disable promijeni status
      row.itemProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
          contextMenu.getItems().get(1).setDisable(newValue.getStatus() == ItemOnOrderStatus.DONE);
        }
      });

      return row;
    });

    getDialogPane().getButtonTypes().addAll(CustomButtonTypes.OK);
  }

  public OrderItems(Order order) {
    this.order = order;
    Navigator.asDialog("order/items.fxml", this);
    signaledTaskThread = new SignaledTaskThread<>(p -> orderRepository.getItemsOnOrder(order.getId()));
    signaledTaskThread.getResultProperty().addListener((observable, oldValue, newValue) -> {
      itemOnOrderTableView.setItems(newValue);
      itemOnOrderTableView.autoResizeColumns();
    });

    setResultConverter(buttonType -> {
      if (buttonType == CustomButtonTypes.OK) {
        return true;
      }
      return true;
    });

    signaledTaskThread.signal();
  }

  private void deleteItem(ItemOnOrder itemOnOrder) {
    ButtonType result = new AppDialog().showConfirmationMessage("Brisanje stavke", "Da li ste sigurni da želite obrisati stavku?", CustomButtonTypes.DELETE);
    if (result != CustomButtonTypes.DELETE) {
      return;
    }
    orderRepository.deleteItemOnOrder(itemOnOrder.getId());
    signaledTaskThread.signal();
  }

  private void addItemToOrder() {
    new AddItemDialog().showAndWait().ifPresent(itemOnOrder -> {
      Order old = order.clone();
      itemOnOrder.forEach(t -> orderRepository.saveItemOnOrder(order, t));
      new ChangeWriterThread<>(new ModifyChange<>(old, order)).start();
      signaledTaskThread.signal();
    });
  }


  private void changeStatus(ItemOnOrder itemOnOrder) {
    // set status to done
    itemOnOrder.setStatus(ItemOnOrderStatus.DONE);
    orderRepository.updateItemOnOrder(itemOnOrder.getId(), itemOnOrder);
    signaledTaskThread.signal();
  }

  private void addAgain(ItemOnOrder itemOnOrder) {
  }

  @Override
  public void cleanup() {
    signaledTaskThread.interrupt();
  }
}
