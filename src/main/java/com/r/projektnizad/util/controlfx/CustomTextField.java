package com.r.projektnizad.util.controlfx;

import atlantafx.base.controls.CustomTextFieldSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;

/*
Sourced from: https://github.com/controlsfx/controlsfx
 */

public class CustomTextField extends TextField {

  /*************************************************************************
   <p>
   Private fields

   */


  /*************************************************************************
   <p>
   Constructors

   */

  /**
   * Instantiates a default CustomTextField.
   */
  public CustomTextField() {
    getStyleClass().add("custom-text-field"); //$NON-NLS-1$
  }


  /**************************************************************************
   * <p>
   * Properties
   *
   **************************************************************************/

  // --- left
  private final ObjectProperty<Node> left = new SimpleObjectProperty<>(this, "left"); //$NON-NLS-1$

  /**
   * @return An ObjectProperty wrapping the {@link Node} that is placed
   * on the left ofthe text field.
   */
  public final ObjectProperty<Node> leftProperty() {
    return left;
  }

  /**
   * @return the {@link Node} that is placed on the left of
   * the text field.
   */
  public final Node getLeft() {
    return left.get();
  }

  /**
   * Sets the {@link Node} that is placed on the left of
   * the text field.
   */
  public final void setLeft(Node value) {
    left.set(value);
  }


  // --- right
  private final ObjectProperty<Node> right = new SimpleObjectProperty<>(this, "right"); //$NON-NLS-1$

  /**
   * Property representing the {@link Node} that is placed on the right of
   * the text field.
   *
   * @return An ObjectProperty.
   */
  public final ObjectProperty<Node> rightProperty() {
    return right;
  }

  /**
   * @return The {@link Node} that is placed on the right of
   * the text field.
   */
  public final Node getRight() {
    return right.get();
  }

  /**
   * Sets the {@link Node} that is placed on the right of
   * the text field.
   */
  public final void setRight(Node value) {
    right.set(value);
  }


  /*************************************************************************
   <p>
   Public API

   */

  /**
   * {@inheritDoc}
   */
  @Override
  protected Skin<?> createDefaultSkin() {
    return new CustomTextFieldSkin(this) {
      @Override
      public ObjectProperty<Node> leftProperty() {
        return CustomTextField.this.leftProperty();
      }

      @Override
      public ObjectProperty<Node> rightProperty() {
        return CustomTextField.this.rightProperty();
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getUserAgentStylesheet() {
    return "";
  }
}