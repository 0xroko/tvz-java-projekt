package com.r.projektnizad.controllers.category;

import com.r.projektnizad.dao.CategoryDao;
import com.r.projektnizad.models.Category;
import com.r.projektnizad.models.StoppableScene;
import com.r.projektnizad.threads.PeriodicUpdaterTask;
import com.r.projektnizad.util.Navigator;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.function.Function;

public class View implements StoppableScene {
  public TableColumn<Category, String> descriptionTableColumn;
  public TableColumn<Category, String> nameTableColumn;
  public TableColumn<Category, Long> idTableColumn;

  private final CategoryDao categoryDao = new CategoryDao();
  public TableView<Category> categoryTableView;


  private Thread updaterThread;


  @FXML
  private void initialize() {
    Navigator.setController(this);
    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
    nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    descriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

    final PeriodicUpdaterTask<List<Category>, Function<Object, List<Category>>> updaterTask = new PeriodicUpdaterTask<>((args) -> categoryDao.getAll(), 3000L);

    updaterTask.valueProperty().addListener((observable, oldValue, newValue) -> {
      categoryTableView.getItems().clear();
      categoryTableView.getItems().addAll(newValue);
    });

    updaterThread = new Thread(updaterTask);
    updaterThread.start();
  }

  @Override
  public void stop() {
    updaterThread.interrupt();
  }
}
