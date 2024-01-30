package com.r.projektnizad.controllers.category;

import com.r.projektnizad.models.Category;
import com.r.projektnizad.util.AppDialog;
import com.r.projektnizad.util.CustomButtonTypes;
import com.r.projektnizad.util.Navigator;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.Optional;

public class AddDialog extends Dialog<Category> {
  public TextField descriptionInput;
  public TextField nameInput;

  public Label titleLabel;

  public AddDialog(Optional<Category> editCategory) {
    Navigator.asDialog("category/add.fxml", this);

    boolean isEdit = editCategory.isPresent();

    if (isEdit) {
      Category category = editCategory.get();
      nameInput.setText(category.getName());
      descriptionInput.setText(category.getDescription());

      titleLabel.setText("Uredi kategoriju");
    }

    setResultConverter(buttonType -> {
      Long id = isEdit ? editCategory.get().getId() : null;
      Category category = new Category(id, nameInput.getText(), descriptionInput.getText());
      if (buttonType == CustomButtonTypes.EDIT) {
        ButtonType confirm = new AppDialog().showConfirmationMessage("Uredi kategoriju", "Da li ste sigurni da želite urediti kategoriju?", CustomButtonTypes.EDIT);
        if (confirm == ButtonType.CANCEL) return null;
        return category;
      }

      if (buttonType == CustomButtonTypes.ADD) {
        return category;
      }
      return null;
    });

    getDialogPane().getButtonTypes().addAll(!isEdit ? CustomButtonTypes.ADD : CustomButtonTypes.EDIT, CustomButtonTypes.CANCEL);
  }

}
