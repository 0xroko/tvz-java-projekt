package com.r.projektnizad.util.controlfx;

import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.NestedTableColumnHeader;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;

import java.util.ArrayList;
import java.util.List;

public class CustomTableViewSkin extends TableViewSkin {
  private List<CustomTableColumnHeader> columnHeadersList = new ArrayList<>();

  private static class CustomTableColumnHeader extends TableColumnHeader {
    /**
     * Creates a new TableColumnHeader instance to visually represent the given
     * {@link TableColumnBase} instance.
     *
     * @param tc The table column to be visually represented by this instance.
     */
    public CustomTableColumnHeader(TableColumnBase tc) {
      super(tc);
    }

    public void resizeColumnToFitContent() {
      super.resizeColumnToFitContent(-1);
    }
  }

  public CustomTableViewSkin(TableView tableView) {
    super(tableView);
  }

  @Override
  protected TableHeaderRow createTableHeaderRow() {
    return new TableHeaderRow(this) {
      @Override
      protected NestedTableColumnHeader createRootHeader() {
        return new NestedTableColumnHeader(null) {
          @Override
          protected TableColumnHeader createTableColumnHeader(TableColumnBase col) {
            CustomTableColumnHeader columnHeader = new CustomTableColumnHeader(col);

            if (columnHeadersList == null) {
              columnHeadersList = new ArrayList<>();
            }

            columnHeadersList.add(columnHeader);

            return columnHeader;
          }
        };
      }
    };
  }

  public void resizeColumnToFit() {
    if (!columnHeadersList.isEmpty()) {
      for (CustomTableColumnHeader columnHeader : columnHeadersList) {
        columnHeader.resizeColumnToFitContent();
      }
    }
  }
}