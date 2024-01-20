package com.r.projektnizad.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Background;

import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;

import java.util.stream.Collectors;

public class Main {

  public TextField input;
  public ComboBox<String> combo;
  @FXML
  private Label problems;

  class RowData {
    private final String col1;
    private final String col2;

    public RowData(String col1, String col2) {
      this.col1 = col1;
      this.col2 = col2;
    }

    public String getCol1() {
      return col1;
    }

    public String getCol2() {
      return col2;
    }
  }

  public TableView<RowData> table;
  public TableColumn<RowData, String> col1;
  public TableColumn<RowData, String> col2;

  private Validator validator = new Validator();

  @FXML
  void initialize() {
    // set random data to table
    col1.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCol1()));
    col2.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCol2()));


    table.setItems(FXCollections.observableArrayList(
      new RowData("1", "2"),
      new RowData("3", "4"),
      new RowData("5", "6")
    ));

    table.setRowFactory(
            (param) -> {
              final TableRow<RowData> row = new TableRow<>();
              final ContextMenu rowMenu = new ContextMenu();
              MenuItem editItem = new MenuItem("Edit");
              MenuItem removeItem = new MenuItem("Delete");
              removeItem.setOnAction(event -> table.getItems().remove(row.getItem()));
              rowMenu.getItems().addAll(editItem, removeItem);

              // only display context menu for non-empty rows:
              row.contextMenuProperty().bind(
                      Bindings.when(row.emptyProperty())
                              .then((ContextMenu) null)
                             .otherwise(rowMenu)
              );
              return row;
            });

    combo.setItems(FXCollections.observableArrayList("1", "2", "3"));


    validator.createCheck()
            .dependsOn("input", input.textProperty())
            .withMethod(c -> {
              String userName = c.get("input");
              if (!userName.toLowerCase().equals(userName)) {
                c.error("Please use only lowercase letters.");
              }
            })
            .decorates(input)
            .immediate();



    createProblemOutput();
  }
  private String getProblemText() {
    return validator.validationResultProperty().get().getMessages().stream()
            .map(msg -> msg.getSeverity().toString() + ": " + msg.getText())
            .collect(Collectors.joining("\n"));
  }
  private void createProblemOutput() {
    StringBinding problemsText = Bindings.createStringBinding(this::getProblemText, validator.validationResultProperty());
    problems.textProperty().bind(problemsText);
  }

  @FXML
  protected void onHelloButtonClick() {
  }
}
