package com.r.projektnizad.controllers;

import com.r.projektnizad.models.CleanableScene;
import com.r.projektnizad.models.Entity;
import com.r.projektnizad.models.change.Change;
import com.r.projektnizad.threads.ChangeReaderThread;
import com.r.projektnizad.util.CustomTableView;
import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public final class HistoryController implements CleanableScene {
  @FXML
  private CustomTableView<Change<Entity>> historyTableView;
  @FXML
  private TableColumn<Change<?>, String> dateTableColumn;
  @FXML
  private TableColumn<Change<?>, String> entityNameTableColumn;
  @FXML
  private TableColumn<Change<?>, String> descriptionTableColumn;
  @FXML
  private TableColumn<Change<?>, Long> idTableColumn;
  @FXML
  private TableColumn<Change<?>, String> userTableColumn;
  @FXML
  private TableColumn<Change<?>, String> typeTableColumn;
  @FXML
  private ComboBox<String> entityFilterCombobox;
  @FXML
  private DatePicker filterDatePicker;
  @FXML
  private ComboBox<String> userFilterComboBox;
  private final String allFilter = "Svi";
  private final ChangeReaderThread changeReaderThread = new ChangeReaderThread();

  private boolean dateChanged = false;

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

  void dateSearch() {
    dateChanged = true;
    Date date = filterDatePicker.getValue() == null ? new Date() : new Date(filterDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    changeReaderThread.updateParamsAndRerun(date);
  }

  public void initialize() {
    dateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))));
    entityNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getActualEntity().getEntityName()));
    userTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getUsername() + " (" + cellData.getValue().getUser().getUserType().getName() + ")"));
    typeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChangeType()));
    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getActualEntity().getId()).asObject());

    changeReaderThread.getResultProperty().addListener((observable, oldValue, newValue) -> {
      if (dateChanged) {
        Platform.runLater(() -> setFiltersFromChanges(newValue));
        dateChanged = false;
      }
      historyTableView.setItems(filter(newValue));
      historyTableView.autoResizeColumns();
    });

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
    entityFilterCombobox.valueProperty().addListener((observable, oldValue, newValue) -> {

    });
    userFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
    });

    dateSearch();
  }

  @Override
  public void cleanup() {
    changeReaderThread.end();
  }
}
