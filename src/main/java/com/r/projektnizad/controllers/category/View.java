package com.r.projektnizad.controllers.category;

import com.r.projektnizad.repositories.CategoryRepository;
import com.r.projektnizad.models.Category;
import com.r.projektnizad.models.CleanableScene;
import com.r.projektnizad.models.change.AddChange;
import com.r.projektnizad.models.change.DeleteChange;
import com.r.projektnizad.models.change.ModifyChange;
import com.r.projektnizad.threads.ChangeWriterThread;
import com.r.projektnizad.threads.SignaledTaskThread;
import com.r.projektnizad.util.*;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class View implements CleanableScene {
  public TableColumn<Category, String> descriptionTableColumn;
  public TableColumn<Category, String> nameTableColumn;
  public TableColumn<Category, Long> idTableColumn;
  private final CategoryRepository categoryRepository = new CategoryRepository();
  public CustomTableView<Category> categoryTableView;
  private final SignaledTaskThread<List<Category>, Map<String, String>> signaledTaskThread = new SignaledTaskThread<>((v) -> categoryRepository.getAll());

  @FXML
  private void initialize() {
    Navigator.setController(this);
    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
    nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    descriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

    Map<String, Consumer<Category>> actions = new LinkedHashMap<>();
    actions.put("Uredi", this::edit);
    actions.put("Obriši", this::delete);

    categoryTableView.setRowFactory(tableView -> {
      TableRow<Category> row = new TableRow<>();
      TableViewContextMenu.build(row, actions);
      return row;
    });

    signaledTaskThread.getResultProperty().addListener((observable, oldValue, newValue) -> {
      categoryTableView.setItems(newValue);
      categoryTableView.autoResizeColumns();
    });

    signaledTaskThread.signal();
  }

  private void edit(Category category) {
    var editDialog = new AddDialog(Optional.ofNullable(category));
    editDialog.showAndWait().ifPresentOrElse(newCategory -> {
      categoryRepository.update(category.getId(), newCategory);
      new ChangeWriterThread<>(new ModifyChange<>(category, newCategory)).start();
      new ChangeWriterThread<>(new ModifyChange<>(category, newCategory)).start();
      signaledTaskThread.signal();
    }, editDialog::close);
  }

  private void delete(Category category) {
    ButtonType confirm = new AppDialog().showConfirmationMessage("Obriši kategoriju", "Da li ste sigurni da želite obrisati kategoriju?", CustomButtonTypes.DELETE);
    if (confirm == CustomButtonTypes.CANCEL) return;

    categoryRepository.delete(category.getId());
    new ChangeWriterThread<>(new DeleteChange<>(category)).start();
    signaledTaskThread.signal();
  }

  @Override
  public void cleanup() {
    signaledTaskThread.interrupt();
  }

  public void openAddCategory(ActionEvent actionEvent) {
    var addDialog = new AddDialog(Optional.empty());
    addDialog.showAndWait().ifPresent(category -> {
      categoryRepository.save(category);
      new ChangeWriterThread<>(new AddChange<>(category)).start();
      signaledTaskThread.signal();
    });

  }

}
