/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.controllers.table;

import com.r.projektnizad.exceptions.DatabaseActionFailException;
import com.r.projektnizad.models.CleanableScene;
import com.r.projektnizad.models.Table;
import com.r.projektnizad.models.change.AddChange;
import com.r.projektnizad.models.change.DeleteChange;
import com.r.projektnizad.models.change.ModifyChange;
import com.r.projektnizad.repositories.TableRepository;
import com.r.projektnizad.threads.ChangeWriterThread;
import com.r.projektnizad.threads.SignaledTaskThread;
import com.r.projektnizad.util.*;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextField;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;


public final class ViewTableController implements CleanableScene {
  @FXML
  private CustomTableView<Table> tableTableView;
  @FXML
  private TableColumn<Table, Long> idTableColumn;
  @FXML
  private TableColumn<Table, String> nameTableColumn;
  @FXML
  private TableColumn<Table, String> descriptionTableColumn;
  @FXML
  private TableColumn<Table, Long> seatsTableColumn;
  @FXML
  private TextField nameSearchTextField;

  private final TableRepository tableRepository = new TableRepository();
  private final SignaledTaskThread<List<Table>, Map<String, Filter.FilterItem>> tableTaskThread = new SignaledTaskThread<>(Util.wrapCheckedFunction(tableRepository::filter));
  private final Map<String, Filter.FilterItem> filters = new LinkedHashMap<>();

  @FXML
  private void initialize() {
    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
    nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    descriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
    seatsTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getSeats()).asObject());

    nameSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.isEmpty()) {
        filters.remove("name|description");
      } else {
        filters.put("name|description", new Filter.FilterItem(newValue, Filter.FilterType.LIKE));
      }
      tableTaskThread.signal(filters);
    });

    Map<String, Consumer<Table>> actions = new LinkedHashMap<>();

    actions.put("Izmjeni", this::editTable);
    actions.put("Obriši", this::deleteTable);

    tableTableView.setRowFactory(tv -> {
      TableRow<Table> row = new TableRow<>();
      TableViewContextMenu.build(row, actions);
      return row;
    });

    tableTaskThread.getResultProperty().addListener((observable, oldValue, newValue) -> {
      tableTableView.setItems(newValue);
      tableTableView.autoResizeColumns();
    });

    tableTaskThread.signal(filters);
  }

  @Override
  public void cleanup() {
    tableTaskThread.interrupt();
  }

  public void deleteTable(Table table) {
    ButtonType result = new AppDialog().showConfirmationMessage("Brisanje stola", "Jeste li sigurni da želite obrisati stol?", CustomButtonTypes.DELETE);
    if (result != CustomButtonTypes.DELETE) return;
    try {
      tableRepository.delete(table.getId());
      new ChangeWriterThread<>(new DeleteChange<>(table)).start();
      tableTaskThread.signal(filters);
    } catch (DatabaseActionFailException e) {
      new AppDialog().showExceptionMessage(e);
    }
  }

  public void openAddTable(ActionEvent actionEvent) {
    Optional<Table> table = new ModifyTableDialog(Optional.empty()).showAndWait();
    if (table.isPresent())
      try {
        tableRepository.save(table.get());
        new ChangeWriterThread<>(new AddChange<>(table.get())).start();
        tableTaskThread.signal(filters);
      } catch (DatabaseActionFailException e) {
        new AppDialog().showExceptionMessage(e);
      }
  }

  public void editTable(Table table) {
    Table old = table.clone();
    new ModifyTableDialog(Optional.of(table)).showAndWait().ifPresent(t -> {
      try {
        tableRepository.update(t.getId(), t);
        new ChangeWriterThread<>(new ModifyChange<>(old, t)).start();
        tableTaskThread.signal(filters);
      } catch (DatabaseActionFailException e) {
        new AppDialog().showExceptionMessage(e);
      }
    });
  }

}
