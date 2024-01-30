package com.r.projektnizad.controllers;

import com.r.projektnizad.exceptions.HistoryEntryNotFound;
import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.Entity;
import com.r.projektnizad.models.history.Change;
import com.r.projektnizad.util.CustomTableView;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class History {
  public CustomTableView<Change<? extends Entity>> historyTableView;
  public TableColumn<Change<?>, String> dateTableColumn;
  public TableColumn<Change<?>, String> entityNameTableColumn;
  public TableColumn<Change<?>, String> descriptionTableColumn;
  public TableColumn<Change<?>, String> idTableColumn;
  public TableColumn<Change<?>, String> userTableColumn;
  public TableColumn<Change<?>, String> typeTableColumn;
  public ComboBox<String> entityFilterCombobox;
  public DatePicker filterDatePicker;
  public ComboBox<String> userFilterComboBox;

  public String allFilter = "Svi";

  void setFiltersFromChanges(ArrayList<Change<Entity>> changes) {
    var entities = new ArrayList<String>();
    var users = new ArrayList<String>();

    for (var change : changes) {
      if (!entities.contains(change.getActualEntity().getEntityName())) {
        entities.add(change.getActualEntity().getEntityName());
      }
      if (!users.contains(change.getUser().getUsername())) {
        users.add(change.getUser().getUsername());
      }
    }

    if (!entities.contains(allFilter)) entities.addFirst(allFilter);
    if (!users.contains(allFilter)) users.addFirst(allFilter);

    entityFilterCombobox.getItems().clear();
    entityFilterCombobox.setItems(FXCollections.observableArrayList(entities));

    userFilterComboBox.getItems().clear();
    userFilterComboBox.setItems(FXCollections.observableArrayList(users));

    entityFilterCombobox.setValue(allFilter);
    userFilterComboBox.setValue(allFilter);

  }

  ArrayList<Change<Entity>> filter(ArrayList<Change<Entity>> changes) {
    var filtered = new ArrayList<Change<Entity>>();

    boolean filterByEntity = !Optional.ofNullable(entityFilterCombobox.getValue()).orElse(allFilter).equals(allFilter);
    boolean filterByUser = !Optional.ofNullable(userFilterComboBox.getValue()).orElse(allFilter).equals(allFilter);

    for (var change : changes) {
      if (filterByEntity && !change.getActualEntity().getEntityName().equals(entityFilterCombobox.getValue())) {
        continue;
      }
      if (filterByUser && !change.getUser().getUsername().equals(userFilterComboBox.getValue())) {
        continue;
      }
      filtered.add(change);
    }

    return filtered;
  }

  ArrayList<Change<Entity>> currentChanges = new ArrayList<>();

  void dateSearch() {
    Date date = filterDatePicker.getValue() == null ? new Date() : new Date(filterDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    try {
      Optional<ArrayList<Change<Entity>>> changes = Main.historyChangeService.readChanges(date);
      if (changes.isEmpty()) {
        return;
      }

      setFiltersFromChanges(changes.get());
      currentChanges = changes.get();
      search();
    } catch (HistoryEntryNotFound e) {
      historyTableView.getItems().clear();
    }
  }

  void search() {
    historyTableView.getItems().clear();
    historyTableView.getItems().addAll(filter(currentChanges));
    historyTableView.autoResizeColumns();
  }


  public void initialize() {
    dateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))));
    entityNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getActualEntity().getEntityName()));
    userTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getUsername()));
    typeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChangeType()));
    idTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getActualEntity().getId().toString()));

    descriptionTableColumn.setCellValueFactory(cellData -> {
      StringBuilder sb = new StringBuilder();
      for (var entry : cellData.getValue().diff().entrySet()) {
        sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
      }
      return new SimpleStringProperty(sb.toString());
    });

    filterDatePicker.setValue(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    entityFilterCombobox.setValue(allFilter);
    userFilterComboBox.setValue(allFilter);

    filterDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> dateSearch());
    entityFilterCombobox.valueProperty().addListener((observable, oldValue, newValue) -> search());
    userFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> search());

    dateSearch();
  }
}
