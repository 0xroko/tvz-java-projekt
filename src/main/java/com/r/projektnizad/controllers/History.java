package com.r.projektnizad.controllers;

import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.Entity;
import com.r.projektnizad.models.history.Change;
import com.r.projektnizad.util.CustomTableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

import java.time.format.DateTimeFormatter;
import java.util.Date;

public class History {
  public CustomTableView<Change<? extends Entity>> historyTableView;
  public TableColumn<Change<?>, String> dateTableColumn;
  public TableColumn<Change<?>, String> entityNameTableColumn;
  public TableColumn<Change<?>, String> descriptionTableColumn;
  public TableColumn<Change<?>, String> userTableColumn;
  public TableColumn<Change<?>, String> typeTableColumn;


  public void initialize() {
    dateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))));
    entityNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getActualEntity().getEntityName()));

    userTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getUsername()));
    typeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChangeType()));

    descriptionTableColumn.setCellValueFactory(cellData -> {
      StringBuilder sb = new StringBuilder();
      for (var entry : cellData.getValue().diff().entrySet()) {
        sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
      }
      return new SimpleStringProperty(sb.toString());
    });

    Main.historyChangeService.readChanges(new Date()).ifPresent(h -> historyTableView.getItems().addAll(h));
  }
}
