package com.r.projektnizad.util;

import javafx.scene.control.Control;

public class ControlClassSetter {
    private final Control control;
    private final String styleClass;

    public ControlClassSetter(Control control, String styleClass) {
      this.control = control;
      this.styleClass = styleClass;
    }
    public void set() {
      control.getStyleClass().add(styleClass);
    }

    public void unset() {
      control.getStyleClass().remove(styleClass);
    }
}
