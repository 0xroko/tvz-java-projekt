package com.r.projektnizad.controllers.category;

import com.r.projektnizad.dao.CategoryDao;
import com.r.projektnizad.models.Category;
import com.r.projektnizad.models.StoppableScene;
import com.r.projektnizad.threads.SignaledTaskThread;
import com.r.projektnizad.util.AppDialog;
import com.r.projektnizad.util.CustomButtonTypes;
import com.r.projektnizad.util.Navigator;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.util.List;
import java.util.Optional;

public class View implements StoppableScene {
  public TableColumn<Category, String> descriptionTableColumn;
  public TableColumn<Category, String> nameTableColumn;
  public TableColumn<Category, Long> idTableColumn;

  private final CategoryDao categoryDao = new CategoryDao();
  public TableView<Category> categoryTableView;

  private final SignaledTaskThread<List<Category>> signaledTaskThread = new SignaledTaskThread<>((v) -> categoryDao.getAll());

  @FXML
  private void initialize() {
    Navigator.setController(this);
    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
    nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    descriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

    // add right click menu for delete and edit
    categoryTableView.setRowFactory(tableView -> {
      var row = new TableRow<Category>();
      row.setOnMouseClicked(event -> {
        if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
          // edit and delete items
          ContextMenu contextMenu = new ContextMenu();
          var editItem = new MenuItem("Izmjeni");
          var deleteItem = new MenuItem("Obriši");

          editItem.setOnAction(event1 -> edit(row.getItem()));
          deleteItem.setOnAction(event1 -> {
            var confirm = new AppDialog().showConfirmationMessage("Obriši kategoriju", "Da li ste sigurni da želite obrisati kategoriju?", CustomButtonTypes.DELETE);
            if (confirm == CustomButtonTypes.DELETE) {
              delete(row.getItem());
            }
          });

          contextMenu.getItems().addAll(editItem, deleteItem);
          row.setContextMenu(contextMenu);
        }
      });
      return row;
    });


    // update with new data, update existing data
    signaledTaskThread.resultProperty().addListener((observable, oldValue, newValue) -> {
      categoryTableView.getItems().clear();
      categoryTableView.getItems().addAll(newValue);
    });

    signaledTaskThread.signal();
  }

  private void edit(Category category) {
    var editDialog = new AddDialog(Optional.ofNullable(category));
    editDialog.showAndWait().ifPresentOrElse(newCategory -> {
      categoryDao.update(category.getId(), newCategory);
      signaledTaskThread.signal();
    }, editDialog::close);
  }

  private void delete(Category category) {
    categoryDao.delete(category.getId());
    signaledTaskThread.signal();
  }

  @Override
  public void stop() {
    signaledTaskThread.interrupt();
  }

  public void openAddCategory(ActionEvent actionEvent) {
    var addDialog = new AddDialog(Optional.empty());

    addDialog.showAndWait().ifPresent(category -> {
      categoryDao.save(category);
      signaledTaskThread.signal();
    });

  }
}
