package com.r.projektnizad.util.controlfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;
/*
Sourced from: https://github.com/controlsfx/controlsfx
 */

public class SearchableComboBox<T> extends ComboBox<T> {

  private static final String DEFAULT_STYLE_CLASS = "searchable-combo-box";

  public SearchableComboBox() {
    this(FXCollections.observableArrayList());
  }

  public SearchableComboBox(ObservableList<T> items) {
    super(items);
    getStyleClass().add(DEFAULT_STYLE_CLASS);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new SearchableComboBoxSkin<>(this);
  }

}