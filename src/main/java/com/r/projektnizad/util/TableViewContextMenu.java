package com.r.projektnizad.util;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.input.KeyCharacterCombination;

import java.util.Map;
import java.util.function.Consumer;

public class TableViewContextMenu {
  public static <T> ContextMenu build(TableRow<T> row, Map<String, Consumer<T>> actions) {
    ContextMenu contextMenu = new ContextMenu();
    actions.forEach((name, action) -> {
      MenuItem menuItem = new MenuItem(name);
      menuItem.setOnAction(event1 -> action.accept(row.getItem()));
      contextMenu.getItems().add(menuItem);
    });

    row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu) null).otherwise(contextMenu));
    contextMenu.setOnShowing(event1 -> {
      if (row.isEmpty()) {
        event1.consume();
      }
    });
    return contextMenu;
  }
}
