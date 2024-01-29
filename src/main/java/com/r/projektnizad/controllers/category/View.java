package com.r.projektnizad.controllers.category;

import com.r.projektnizad.dao.CategoryDao;
import com.r.projektnizad.models.Category;
import com.r.projektnizad.models.CleanableScene;
import com.r.projektnizad.models.history.AddChange;
import com.r.projektnizad.models.history.DeleteChange;
import com.r.projektnizad.models.history.ModifyChange;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class View implements CleanableScene {
  public TableColumn<Category, String> descriptionTableColumn;
  public TableColumn<Category, String> nameTableColumn;
  public TableColumn<Category, Long> idTableColumn;
  private final CategoryDao categoryDao = new CategoryDao();
  public CustomTableView<Category> categoryTableView;
  private final SignaledTaskThread<List<Category>> signaledTaskThread = new SignaledTaskThread<>((v) -> categoryDao.getAll());

  @FXML
  private void initialize() {
    Navigator.setController(this);
    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
    nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    descriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

    Map<String, Consumer<Category>> actions = Map.of(
            "Izmjeni", this::edit,
            "Obriši", this::delete
    );

    categoryTableView.setRowFactory(tableView -> {
      TableRow<Category> row = new TableRow<>();
      TableViewContextMenu.build(row, actions);
      return row;
    });

    signaledTaskThread.resultProperty().addListener((observable, oldValue, newValue) -> {
      categoryTableView.setItems(newValue);
    });

    signaledTaskThread.signal();
  }

  private void edit(Category category) {
    var editDialog = new AddDialog(Optional.ofNullable(category));
    editDialog.showAndWait().ifPresentOrElse(newCategory -> {
      categoryDao.update(category.getId(), newCategory);
      new ChangeWriterThread<>(new ModifyChange<>(category, newCategory)).start();
      signaledTaskThread.signal();
    }, editDialog::close);
  }

  private void delete(Category category) {
    ButtonType confirm = new AppDialog().showConfirmationMessage("Obriši kategoriju", "Da li ste sigurni da želite obrisati kategoriju?", CustomButtonTypes.DELETE);
    if (confirm == CustomButtonTypes.CANCEL) return;

    categoryDao.delete(category.getId());
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
      categoryDao.save(category);
      new ChangeWriterThread<>(new AddChange<>(category)).start();
      signaledTaskThread.signal();
    });

  }
}
