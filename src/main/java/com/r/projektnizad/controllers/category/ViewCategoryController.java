package com.r.projektnizad.controllers.category;

import com.r.projektnizad.exceptions.DatabaseActionFailException;
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

public final class ViewCategoryController implements CleanableScene {
  public TableColumn<Category, String> descriptionTableColumn;
  public TableColumn<Category, String> nameTableColumn;
  public TableColumn<Category, Long> idTableColumn;
  public TextField nameSearchTextField;
  private final CategoryRepository categoryRepository = new CategoryRepository();
  public CustomTableView<Category> categoryTableView;

  private final Map<String, Filter.FilterItem> filters = new LinkedHashMap<>();
  private final SignaledTaskThread<List<Category>, Map<String, Filter.FilterItem>> categoryTaskThread = new SignaledTaskThread<>(
          Util.wrapCheckedFunction(categoryRepository::filter)
  );

  @FXML
  private void initialize() {
    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
    nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    descriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

    nameSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.isEmpty()) {
        filters.remove("name|description");
      } else {
        filters.put("name|description", new Filter.FilterItem(newValue, Filter.FilterType.LIKE));
      }
      categoryTaskThread.signal(filters);
    });

    Map<String, Consumer<Category>> actions = new LinkedHashMap<>();
    actions.put("Uredi", this::edit);
    actions.put("Obriši", this::delete);

    categoryTableView.setRowFactory(tableView -> {
      TableRow<Category> row = new TableRow<>();
      TableViewContextMenu.build(row, actions);
      return row;
    });

    categoryTaskThread.getResultProperty().addListener((observable, oldValue, newValue) -> {
      categoryTableView.setItems(newValue);
      categoryTableView.autoResizeColumns();
    });

    categoryTaskThread.signal(filters);
  }

  private void edit(Category category) {
    new ModifyCategoryDialog(Optional.ofNullable(category)).showAndWait().ifPresent(newCategory -> {
      try {
        categoryRepository.update(category.getId(), newCategory);
        new ChangeWriterThread<>(new ModifyChange<>(category, newCategory)).start();
        categoryTaskThread.signal();
      } catch (DatabaseActionFailException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private void delete(Category category) {
    ButtonType confirm = new AppDialog().showConfirmationMessage("Obriši kategoriju", "Da li ste sigurni da želite obrisati kategoriju?", CustomButtonTypes.DELETE);
    if (confirm == CustomButtonTypes.CANCEL) return;

    try {
      categoryRepository.delete(category.getId());
      new ChangeWriterThread<>(new DeleteChange<>(category)).start();
      categoryTaskThread.signal();
    } catch (DatabaseActionFailException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void cleanup() {
    categoryTaskThread.interrupt();
  }

  public void openAddCategory() {
    var addDialog = new ModifyCategoryDialog(Optional.empty());
    addDialog.showAndWait().ifPresent(category -> {
      try {
        categoryRepository.save(category);
        new ChangeWriterThread<>(new AddChange<>(category)).start();
        categoryTaskThread.signal();
      } catch (DatabaseActionFailException e) {
        new AppDialog().showExceptionMessage(e);
      }
    });

  }

}
