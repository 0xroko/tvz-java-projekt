package com.r.projektnizad.util;

import com.r.projektnizad.util.controlfx.CustomTableViewSkin;
import javafx.application.Platform;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class CustomTableView<T> extends TableView<T> {
  private Number lastScrollOffset = 0;
  private ScrollBar scrollBar;
  private boolean currentlyCleaning = false;

  private final CustomTableViewSkin thisSkin;

  public CustomTableView() {
    super();
    setSkin(thisSkin = new CustomTableViewSkin(this));

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
    Platform.runLater(() -> {
      if (thisSkin != null && getSkin() == thisSkin && !this.getItems().isEmpty()) {
        thisSkin.resizeColumnToFit();
      }
    });
  }

}
