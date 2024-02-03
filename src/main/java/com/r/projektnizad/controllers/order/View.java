/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.order;

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

public class View implements CleanableScene {
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

  private final OrderRepository orderRepository = new OrderRepository();
  private final SignaledTaskThread<List<Order>, Map<String, Filter.FilterItem>> signaledTaskThread = new SignaledTaskThread<>(orderRepository::filter);
  private final Map<String, Filter.FilterItem> filters = new HashMap<>();

  public void initialize() {
    Navigator.setController(this);
    orderStatusComboBox.setItems(FXCollections.observableArrayList(OrderStatus.values()));

    orderStatusComboBox.getSelectionModel().selectFirst();
    Util.comboBoxCellFactorySetters(orderStatusComboBox, OrderStatus::getName);

    orderDateDatePicker.setValue(LocalDateTime.now().toLocalDate());
    orderDateDatePicker.getEditor().setDisable(true);
    orderDateDatePicker.getEditor().setOpacity(1);

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

    Map<String, Consumer<Order>> actions = new LinkedHashMap<>();
    actions.put("Pregledaj", this::openOrderView);
    actions.put("Izmijeni", this::openOrderEdit);
    actions.put("Obriši", this::deleteOrder);

    orderTableView.setRowFactory(tableView -> {
      TableRow<Order> row = new TableRow<>();
      row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && !row.isEmpty()) {
          openOrderView(row.getItem());
        }
      });
      TableViewContextMenu.build(row, actions);
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
        filters.put("status", new Filter.FilterItem(newValue.getCode().toString(), Filter.FilterType.EQUAL));
      } else {
        filters.remove("status");
      }
      signaledTaskThread.signal(filters);
    });

    signaledTaskThread.getResultProperty().addListener((observable, oldValue, newValue) -> {
      orderTableView.setItems(newValue);
      orderTableView.autoResizeColumns();
    });

    orderDateDatePicker.setValue(LocalDateTime.now().toLocalDate());

    signaledTaskThread.signal(filters);
  }

  private void openOrderView(Order actionEvent) {
  }

  private void openOrderEdit(Order order) {
    new AddDialog(Optional.of(order)).showAndWait().ifPresent(order1 -> {
      orderRepository.update(order1.getId(), order1);
      new ChangeWriterThread<>(new ModifyChange<>(order, order1)).start();
      signaledTaskThread.signal();
    });
  }

  private void deleteOrder(Order order) {
    ButtonType result = new AppDialog().showConfirmationMessage("Brisanje narudžbe", "Da li ste sigurni da želite obrisati narudžbu?", CustomButtonTypes.DELETE);
    if (result != CustomButtonTypes.DELETE) return;
    orderRepository.delete(order.getId());
    new ChangeWriterThread<>(new DeleteChange<>(order)).start();
    signaledTaskThread.signal();
  }

  public void openOrderAdd(ActionEvent actionEvent) {
    new AddDialog(Optional.empty()).showAndWait().ifPresent(order -> {
      orderRepository.save(order);
      new ChangeWriterThread<>(new AddChange<>(order)).start();
      signaledTaskThread.signal();
    });
  }

  @Override
  public void cleanup() {
    signaledTaskThread.interrupt();
  }
}
