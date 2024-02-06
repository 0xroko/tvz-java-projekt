package com.r.projektnizad.util;

import javafx.scene.control.ButtonType;

public class CustomButtonTypes {

  public static final ButtonType OK = new ButtonType("OK");
  public static ButtonType YES = new ButtonType("Da");
  public static ButtonType NO = new ButtonType("Ne");
  public static final ButtonType DELETE = new ButtonType("Obriši");
  public static final ButtonType EDIT = new ButtonType("Uredi");
  public static final ButtonType ADD = new ButtonType("Dodaj");
  public static final ButtonType CANCEL = new ButtonType("Odustani", ButtonType.CANCEL.getButtonData());
}
