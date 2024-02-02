package com.r.projektnizad.controllers.item;

import com.r.projektnizad.dao.CategoryDao;
import com.r.projektnizad.dao.ItemDao;
import com.r.projektnizad.models.Category;
import com.r.projektnizad.models.CleanableScene;
import com.r.projektnizad.models.Item;
import com.r.projektnizad.models.change.AddChange;
import com.r.projektnizad.models.change.DeleteChange;
import com.r.projektnizad.models.change.ModifyChange;
import com.r.projektnizad.threads.ChangeWriterThread;
import com.r.projektnizad.threads.SignaledTaskThread;
import com.r.projektnizad.util.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

public class View implements CleanableScene {
  public CustomTableView<Item> itemTableView;
  public TableColumn<Item, Long> idTableColumn;
  public TableColumn<Item, String> nameTableColumn;
  public TableColumn<Item, Double> priceTableColumn;
  public TableColumn<Item, Long> stockTableColumn;
  public TableColumn<Item, String> categoryTableColumn;
  public TableColumn<Item, String> descriptionTableColumn;
  public TableColumn<Item, String> preparationTimeTableColumn;
  public TextField nameSearchTextField;
  public ComboBox<Category> categorySearchComboBox;

  private final Map<String, Filter.FilterItem> filterMap = new HashMap<>();
  private final ItemDao itemDao = new ItemDao();
  private final SignaledTaskThread<List<Item>, Map<String, Filter.FilterItem>> signaledTaskThread = new SignaledTaskThread<>(itemDao::filter);

  void refresh() {
    signaledTaskThread.signal(filterMap);
  }

  public void initialize() {

    // bind search text field to filter map
    nameSearchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.isEmpty()) {
        filterMap.remove("item.name");
      } else {
        filterMap.put("item.name", new Filter.FilterItem(newValue, Filter.FilterType.LIKE));
      }
      refresh();
    });

    categorySearchComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null || newValue.getName().equals("Sve")) {
        filterMap.remove("item.category_id");
      } else {
        filterMap.put("item.category_id", new Filter.FilterItem(String.valueOf(newValue.getId()), Filter.FilterType.EQUAL));
      }
      refresh();
    });

    categorySearchComboBox.setItems(FXCollections.observableArrayList(new CategoryDao().getAll()));
    categorySearchComboBox.getItems().addFirst(new Category(-1L, "Sve", "Sve kategorije"));
    Util.comboBoxCellFactorySetters(categorySearchComboBox, Category::getName);

    idTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getId()).asObject());
    nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    priceTableColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice().doubleValue()).asObject());
    stockTableColumn.setCellValueFactory(cellData -> new SimpleLongProperty(cellData.getValue().getStock()).asObject());
    categoryTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory().getName()));
    descriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));

    preparationTimeTableColumn.setCellValueFactory(cellData -> {
      if (cellData.getValue().getPreparationTime().equals(Duration.ZERO)) {
        return new SimpleStringProperty("ODMAH");
      }
      return new SimpleStringProperty(Util.formatDuration(cellData.getValue().getPreparationTime()));
    });

    signaledTaskThread.getResultProperty().addListener((observable, oldValue, newValue) -> {
      itemTableView.setItems(newValue);
      itemTableView.autoResizeColumns();
    });

    Map<String, Consumer<Item>> actions = new LinkedHashMap<>();
    actions.put("Povećaj zalihe", this::stockReplenish);
    actions.put("Izmijeni", this::edit);
    actions.put("Obriši", this::delete);

    itemTableView.setRowFactory(tableView -> {
      TableRow<Item> row = new TableRow<>();
      TableViewContextMenu.build(row, actions);
      return row;
    });

    refresh();
  }

  private void edit(Item item) {
    new AddDialog(Optional.of(item)).showAndWait().ifPresent(editedItem -> {
      itemDao.update(editedItem.getId(), editedItem);
      new ChangeWriterThread<>(new ModifyChange<>(item, editedItem)).start();
      refresh();
    });
  }

  private void stockReplenish(Item item) {
    Item oldItem = item.clone();
    item.setStock(item.getDefaultStockIncrement() + item.getStock());
    itemDao.update(item.getId(), item);
    new ChangeWriterThread<>(new ModifyChange<>(oldItem, item)).start();
    refresh();
  }

  private void delete(Item item) {
    ButtonType confirm = new AppDialog().showConfirmationMessage("Obriši artikl", "Da li ste sigurni da želite obrisati artikl?", CustomButtonTypes.DELETE);
    if (confirm != CustomButtonTypes.DELETE) {
      return;
    }
    itemDao.delete(item.getId());
    new ChangeWriterThread<>(new DeleteChange<>(item)).start();
    refresh();
  }

  public void openAddItem() {
    new AddDialog(Optional.empty()).showAndWait().ifPresent(item -> {
      itemDao.save(item);
      new ChangeWriterThread<>(new AddChange<>(item)).start();
      refresh();
    });
  }

  @Override
  public void cleanup() {
    signaledTaskThread.interrupt();
  }

  public void search(KeyEvent keyEvent) {
  }
}
