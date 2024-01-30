package com.r.projektnizad.util;

import javafx.application.Platform;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.util.List;

public class CustomTableView<T> extends TableView<T> {
  private int lastModifiedIndex = 0;

  public CustomTableView() {
    super();
  }

  public void setItems(List<T> items) {
    this.getItems().clear();
    this.getItems().addAll(items);
    Platform.runLater(() -> this.scrollTo(lastModifiedIndex));
  }

  public int getLastModifiedIndex() {
    return lastModifiedIndex;
  }

  public void setLastModifiedIndex(int lastModifiedIndex) {
    this.lastModifiedIndex = lastModifiedIndex;
  }

  public void autoResizeColumns() {
    //Set the right policy
    this.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
    this.getColumns().stream().forEach((column) ->
    {
      //Minimal width = columnheader
      Text t = new Text(column.getText());
      double max = t.getLayoutBounds().getWidth();
      for (int i = 0; i < this.getItems().size(); i++) {
        //cell must not be empty
        if (column.getCellData(i) != null) {
          t = new Text(column.getCellData(i).toString());
          double calcwidth = t.getLayoutBounds().getWidth();
          //remember new max-width
          if (calcwidth > max) {
            max = calcwidth;
          }
        }
      }
      //set the new max-widht with some extra space
      column.setPrefWidth(max + 40.0d);
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
