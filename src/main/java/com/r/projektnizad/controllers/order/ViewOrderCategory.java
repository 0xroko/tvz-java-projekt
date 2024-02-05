/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.order;

import com.r.projektnizad.enums.UserType;
import com.r.projektnizad.exceptions.DatabaseActionFailException;
import com.r.projektnizad.models.change.AddChange;
import com.r.projektnizad.models.change.DeleteChange;
import com.r.projektnizad.models.change.ModifyChange;
import com.r.projektnizad.repositories.OrderRepository;
import com.r.projektnizad.enums.OrderStatus;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.CleanableScene;
import com.r.projektnizad.models.Order;
import com.r.projektnizad.models.User;
import com.r.projektnizad.threads.ChangeWriterThread;
import com.r.projektnizad.threads.SignaledTaskThread;
import com.r.projektnizad.util.*;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

public final class ViewOrderCategory implements CleanableScene {
  public DatePicker orderDateDatePicker;
  public CustomTableView<Order> orderTableView;
  public ComboBox<OrderStatus> orderStatusComboBox;
  public TableColumn<Order, Long> idTableColumn;
  public TableColumn<Order, LocalDateTime> dateTableColumn;
  public TableColumn<Order, String> statusTableColumn;
  public TableColumn<Order, String> userTableColumn;
  public TableColumn<Order, BigDecimal> priceTableColumn;
  public TableColumn<Order, Long> numberOfItemsTableColumn;
  public TableColumn<Order, String> tableTableColumn;
  public TableColumn<Order, String> noteTableColumn;
  public TableColumn<Order, String> itemsTableColumn;
  private final OrderRepository orderRepository = new OrderRepository();
  private final SignaledTaskThread<List<Order>, Map<String, Filter.FilterItem>> signaledTaskThread = new SignaledTaskThread<>(Util.wrapCheckedFunction(orderRepository::filter));
  private final Map<String, Filter.FilterItem> filters = new HashMap<>();
  static private final String startButton = "Započni";
  static private final String doneButton = "Završi";
  static private final String editButton = "Izmijeni";
  static private final String deleteButton = "Obriši";
  static private final String viewButton = "Pregledaj";

  public void initialize() {
    orderStatusComboBox.setItems(FXCollections.observableArrayList(OrderStatus.values()));
    orderStatusComboBox.getSelectionModel().selectFirst();
    Util.comboBoxCellFactorySetter(orderStatusComboBox, OrderStatus::getName);

    orderDateDatePicker.setValue(LocalDateTime.now().toLocalDate());
    orderDateDatePicker.getEditor().setDisable(true);
    orderDateDatePicker.getEditor().setOpacity(1);
    // bug where setting the value of the date picker doesn't trigger the listener
    filters.put("order_time", new Filter.FilterItem(Filter.formatLocalDateTime(orderDateDatePicker.getValue().atStartOfDay()), Filter.FilterType.DATE));

    // only display current user's orders if not admin
    if (!Main.authService.getCurrentUser().get().getUserType().equals(UserType.ADMIN)) {
      filters.put("order.user_id", new Filter.FilterItem(Main.authService.getCurrentUser().get().getId().toString(), Filter.FilterType.EQUAL));
    }

    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
    dateTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getOrderTime()));
    Util.tableViewCellFactory(dateTableColumn, Util::formatDateTime);
    statusTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStatus().getName()));
    priceTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getOrderPriceSum()));
    noteTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getNote()));
    numberOfItemsTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getOrderItemsCount()).asObject());
    tableTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTable().getName()));
    userTableColumn.setCellValueFactory(cellData -> {
      if (cellData.getValue().getUserId() == null) {
        return new SimpleObjectProperty<>("NEPOZNAT");
      } else {
        Optional<User> u = Main.authService.getUserById(cellData.getValue().getUserId());
        return new SimpleObjectProperty<>(u.map(User::getUsername).orElse("NEPOZNAT"));
      }
    });
    itemsTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getOrderItemsString()));

    Map<String, Consumer<Order>> actions = new LinkedHashMap<>();

    actions.put(viewButton, this::openOrderView);
    actions.put(startButton, this::setOrderInProgress);
    actions.put(doneButton, this::setOrderDone);
    actions.put(editButton, this::openOrderEdit);
    actions.put(deleteButton, this::deleteOrder);

    orderTableView.setRowFactory(tableView -> {
      TableRow<Order> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && !row.isEmpty()) {
          openOrderView(row.getItem());
        }
      });
      ContextMenu contextMenu = TableViewContextMenu.build(row, actions);
      // remove start and done from context menu if the order is already in that state
      row.itemProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
          if (newValue.getStatus() == OrderStatus.IN_PROGRESS) {
            contextMenu.getItems().removeIf(item -> item.getText().equals(startButton));
          } else if (newValue.getStatus() == OrderStatus.DONE) {
            contextMenu.getItems().removeIf(item -> item.getText().equals(doneButton));
            contextMenu.getItems().removeIf(item -> item.getText().equals(startButton));
            contextMenu.getItems().removeIf(item -> item.getText().equals(editButton));
          } else if (newValue.getStatus() == OrderStatus.RESERVED) {
            contextMenu.getItems().removeIf(item -> item.getText().equals(doneButton));
          }
        }
      });
      return row;
    });

    orderDateDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        filters.put("order_time", new Filter.FilterItem(Filter.formatLocalDateTime(newValue.atStartOfDay()), Filter.FilterType.DATE));
      } else {
        filters.remove("order_time");
      }
      signaledTaskThread.signal(filters);
    });

    orderStatusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && !newValue.equals(OrderStatus.ALL)) {
        filters.put("order.status", new Filter.FilterItem(newValue.getCode().toString(), Filter.FilterType.EQUAL));
      } else {
        filters.remove("order.status");
      }
      signaledTaskThread.signal(filters);
    });

    signaledTaskThread.getResultProperty().addListener((observable, oldValue, newValue) -> {
      orderTableView.setItems(newValue);
      orderTableView.autoResizeColumns();
    });

    signaledTaskThread.signal(filters);
  }

  private void openOrderView(Order order) {
    new ViewOrderItemsDialog(order).showAndWait().ifPresent(t -> signaledTaskThread.signal()
    );
  }

  private void openOrderEdit(Order order) {
    new ModifyOrderDialog(Optional.of(order)).showAndWait().ifPresent(order1 -> {
      try {
        orderRepository.update(order1.getId(), order1);
        new ChangeWriterThread<>(new ModifyChange<>(order, order1)).start();
        signaledTaskThread.signal();
      } catch (DatabaseActionFailException e) {
        new AppDialog().showExceptionMessage(e);
      }

    });
  }

  private void deleteOrder(Order order) {
    ButtonType result = new AppDialog().showConfirmationMessage("Brisanje narudžbe", "Da li ste sigurni da želite obrisati narudžbu?", CustomButtonTypes.DELETE);
    if (result != CustomButtonTypes.DELETE) return;
    try {
      orderRepository.delete(order.getId());
      new ChangeWriterThread<>(new DeleteChange<>(order)).start();
      signaledTaskThread.signal();
    } catch (DatabaseActionFailException e) {
      new AppDialog().showExceptionMessage(e);
    }

  }

  public void openOrderAdd(ActionEvent actionEvent) {
    new ModifyOrderDialog(Optional.empty()).showAndWait().ifPresent(order -> {
      try {
        orderRepository.save(order);
        new ChangeWriterThread<>(new AddChange<>(order)).start();
        signaledTaskThread.signal();
      } catch (DatabaseActionFailException e) {
        new AppDialog().showExceptionMessage(e);
      }
    });
  }

  public void setOrderInProgress(Order order) {
    Order old = order.clone();
    order.setStatus(OrderStatus.IN_PROGRESS);
    try {
      orderRepository.update(order.getId(), order);
      new ChangeWriterThread<>(new ModifyChange<>(old, order)).start();
      signaledTaskThread.signal();
    } catch (DatabaseActionFailException e) {
      new AppDialog().showExceptionMessage(e);
    }

  }

  public void setOrderDone(Order order) {
    Order old = order.clone();
    order.setStatus(OrderStatus.DONE);
    try {
      orderRepository.update(order.getId(), order);
      new ChangeWriterThread<>(new ModifyChange<>(old, order)).start();
      signaledTaskThread.signal();
    } catch (DatabaseActionFailException e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public void cleanup() {
    signaledTaskThread.interrupt();
  }
}
