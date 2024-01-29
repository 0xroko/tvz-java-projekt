package com.r.projektnizad.util;

import javafx.application.Platform;
import javafx.scene.control.TableView;

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


}
