package com.r.projektnizad.util;

import javafx.application.Platform;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.util.List;

public class CustomTableView<T> extends TableView<T> {
  private Number lastScrollOffset = 0;
  private ScrollBar scrollBar;
  private boolean currentlyCleaning = false;

  public CustomTableView() {
    super();
    Platform.runLater(() -> {
      scrollBar = (ScrollBar) this.lookup(".scroll-bar:vertical");
      if (scrollBar == null) {
        return;
      }
      scrollBar.valueProperty().addListener((observable, oldValue, newValue) -> {
        if (currentlyCleaning) {
          return;
        }

        lastScrollOffset = newValue;
      });
    });
  }

  public void setItems(List<T> items) {
    currentlyCleaning = true;
    this.getItems().clear();
    this.getItems().addAll(items);
    Platform.runLater(() -> {
      if (this.scrollBar == null) {
        return;
      }
      this.scrollBar.setValue(lastScrollOffset.doubleValue());
      currentlyCleaning = false;
    });

  }

  public int getLastScrollOffset() {
    return lastScrollOffset.intValue();
  }

  public void setLastScrollOffset(int lastScrollOffset) {
    this.lastScrollOffset = lastScrollOffset;
  }

  public void autoResizeColumns() {
    //Set the right policy
    this.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    this.getColumns().stream().forEach((column) -> {
      //Minimal width = columnheader
      Text t = new Text(column.getText());
      t.getStyleClass().add("column-header");
      double max = t.getLayoutBounds().getWidth() * 1.2;
      for (int i = 0; i < this.getItems().size(); i++) {
        //cell must not be empty
        if (column.getCellData(i) != null) {
          t = new Text(column.getCellData(i).toString());
          t.getStyleClass().add("cell");
          // if "Datum/Vrijeme" column, set max width to 150
          if (column.getText().equals("Datum/Vrijeme")) {
            max = 150;
          }
          double calcwidth = t.getLayoutBounds().getWidth();
          //remember new max-width
          if (calcwidth > max) {
            max = calcwidth;
          }
        }
      }
      //set the new max-widht with some extra space
      // if macos set padding 80
      if (System.getProperty("os.name").toLowerCase().contains("mac")) {
        column.setPrefWidth(max + 60.0d);
      } else {
        column.setPrefWidth(max + 23);
      }
    });


    // if last column is too small, stretch it
    TableColumn<?, ?> lastColumn = this.getColumns().get(this.getColumns().size() - 1);
    double totalWidth = 0.0d;
    for (TableColumn<?, ?> column : this.getColumns()) {
      totalWidth += column.getWidth();
    }
    if (totalWidth < this.getWidth()) {
      lastColumn.setPrefWidth(lastColumn.getPrefWidth() + this.getWidth() - totalWidth - 20.0d);
    }
  }

}
