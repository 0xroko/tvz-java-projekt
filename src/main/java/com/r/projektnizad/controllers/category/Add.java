package com.r.projektnizad.controllers.category;

import com.r.projektnizad.dao.CategoryDao;
import com.r.projektnizad.models.Category;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class Add {
  public TextField descriptionInput;
  public TextField nameInput;
  private final CategoryDao categoryDao = new CategoryDao();

  public void initialize() {

  }

  public void add(ActionEvent actionEvent) {
    categoryDao.save(new Category(null, nameInput.getText(), descriptionInput.getText()));
  }
}
