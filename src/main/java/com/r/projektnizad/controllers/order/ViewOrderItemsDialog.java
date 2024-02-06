/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.order;

import com.r.projektnizad.enums.ItemOnOrderStatus;
import com.r.projektnizad.enums.OrderStatus;
import com.r.projektnizad.exceptions.DatabaseActionFailException;
import com.r.projektnizad.models.CleanableScene;
import com.r.projektnizad.models.ItemOnOrder;
import com.r.projektnizad.models.Order;
import com.r.projektnizad.models.change.ModifyChange;
import com.r.projektnizad.repositories.OrderRepository;
import com.r.projektnizad.threads.ChangeWriterThread;
import com.r.projektnizad.threads.SignaledTaskThread;
import com.r.projektnizad.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class ViewOrderItemsDialog extends Dialog<Boolean> implements CleanableScene {
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

  final Order order;
  final OrderRepository orderRepository = new OrderRepository();
  final SignaledTaskThread<List<ItemOnOrder>, Void> orderTaskThread;

  @FXML
  void initialize() {
    addItemToOrderButton.setOnAction(event -> addItemToOrder());

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
      row.itemProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
          // if done, disable promijeni status
          contextMenu.getItems().get(1).setDisable(newValue.getStatus() == ItemOnOrderStatus.DONE);
          // if order is not in progress, disable all
          contextMenu.getItems().forEach(item -> item.setDisable(order.getStatus() != OrderStatus.IN_PROGRESS));
        }
      });
      return row;
    });

    getDialogPane().getButtonTypes().addAll(CustomButtonTypes.OK);
  }

  public ViewOrderItemsDialog(Order order) {
    this.order = order;
    Navigator.asDialog("order/items.fxml", this);
    orderTaskThread = new SignaledTaskThread<>(Util.wrapCheckedFunction(p -> orderRepository.getItemsOnOrder(order.getId())));
    orderTaskThread.getResultProperty().addListener((observable, oldValue, newValue) -> {
      itemOnOrderTableView.setItems(newValue);
      order.setItemsOnOrder((ArrayList<ItemOnOrder>) newValue);
      itemOnOrderTableView.autoResizeColumns();
    });

    setResultConverter(buttonType -> true);

    addItemToOrderButton.setDisable(order.getStatus() != OrderStatus.IN_PROGRESS);
    orderTaskThread.signal();
  }

  private void deleteItem(ItemOnOrder itemOnOrder) {
    ButtonType result = new AppDialog().showConfirmationMessage("Brisanje stavke", "Da li ste sigurni da želite obrisati stavku?", CustomButtonTypes.DELETE);
    if (result != CustomButtonTypes.DELETE) {
      return;
    }
    try {
      Order old = order.clone();
      orderRepository.deleteItemOnOrder(itemOnOrder.getId(), order);
      new ChangeWriterThread<>(new ModifyChange<>(old, order)).start();
      orderTaskThread.signal();
    } catch (DatabaseActionFailException e) {
      new AppDialog().showExceptionMessage(e);
    }
  }

  private void addItemToOrder() {
    new ModifyOrderItemsDialog().showAndWait().ifPresent(itemOnOrder -> {
      Order old = order.clone();
      for (ItemOnOrder t : itemOnOrder) {
        try {
          orderRepository.saveItemOnOrder(order, t);
        } catch (DatabaseActionFailException e) {
          new AppDialog().showExceptionMessage(e);
        }
      }
      new ChangeWriterThread<>(new ModifyChange<>(old, order)).start();
      orderTaskThread.signal();
    });
  }

  private void changeStatus(ItemOnOrder itemOnOrder) {
    Order old = order.clone();
    // set status to done
    itemOnOrder.setStatus(ItemOnOrderStatus.DONE);
    try {
      orderRepository.updateItemOnOrder(itemOnOrder.getId(), itemOnOrder);
      new ChangeWriterThread<>(new ModifyChange<>(old, order)).start();
      orderTaskThread.signal();
    } catch (DatabaseActionFailException e) {
      new AppDialog().showExceptionMessage(e);
    }
  }

  private void addAgain(ItemOnOrder itemOnOrder) {
    ItemOnOrder newItemOnOrder = itemOnOrder.clone();
    Order old = order.clone();
    //newItemOnOrder.setStatus(ItemOnOrderStatus.PREPARING);
    newItemOnOrder.setId(null);
    try {
      orderRepository.saveItemOnOrder(order, itemOnOrder);
      new ChangeWriterThread<>(new ModifyChange<>(old, order)).start();
      orderTaskThread.signal();
    } catch (DatabaseActionFailException e) {
      new AppDialog().showExceptionMessage(e);
    }
  }

  @Override
  public void cleanup() {
    orderTaskThread.interrupt();
  }
}
