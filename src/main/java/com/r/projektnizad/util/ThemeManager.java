package com.r.projektnizad.util;

import com.r.projektnizad.main.Main;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/*
 * Adapted from:
 * https://github.com/mkpaz/atlantafx/blob/07f36be7add7f93b8e8457fe248fdbe568b26ccb/sampler/src/main/java/atlantafx/sampler/theme/ThemeManager.java
 * */
public class ThemeManager {
  public static final int DEFAULT_FONT_SIZE = 14;
  public static final int DEFAULT_ZOOM = 100;
  public static final List<Integer> SUPPORTED_ZOOM = List.of(50, 75, 80, 90, 100, 110, 125, 150, 175, 200);
  private static final PseudoClass USER_CUSTOM = PseudoClass.getPseudoClass("user-custom");
  private AccentColor accentColor;

  public record AccentColor(Color primaryColor, PseudoClass pseudoClass) {

    public static AccentColor primerPurple() {
      return new AccentColor(Color.web("#8250df"), PseudoClass.getPseudoClass("accent-primer-purple"));
    }

    public static AccentColor primerPink() {
      return new AccentColor(Color.web("#bf3989"), PseudoClass.getPseudoClass("accent-primer-pink"));
    }

    public static AccentColor primerCoral() {
      return new AccentColor(Color.web("#c4432b"), PseudoClass.getPseudoClass("accent-primer-coral"));
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(ThemeManager.class);
  private final Map<String, String> customCSSDeclarations = new LinkedHashMap<>(); // -fx-property | value;
  private final Map<String, String> customCSSRules = new LinkedHashMap<>(); // .foo | -fx-property: value;
  private String fontFamily = "Inter Bold";
  private Scene scene;

  public void setFontSize(int size) {
    setCustomDeclaration("-fx-font-size", size + "px");
    setCustomRule(".ikonli-font-icon", String.format("-fx-icon-size: %dpx;", size + 2));

    var rawZoom = (int) Math.ceil((size * 1.0 / DEFAULT_FONT_SIZE) * 100);
    int zoom = SUPPORTED_ZOOM.stream()
            .min(Comparator.comparingInt(i -> Math.abs(i - rawZoom)))
            .orElseThrow(NoSuchElementException::new);

    reloadCustomCSS();
  }

  public void setAccentColor(AccentColor color) {
    Objects.requireNonNull(color);


    if (accentColor != null) {
      getScene().getRoot().pseudoClassStateChanged(accentColor.pseudoClass(), false);
    }

    getScene().getRoot().pseudoClassStateChanged(color.pseudoClass(), true);
    this.accentColor = color;
  }

  private boolean set = false;

  public void setScene(Scene s) {
    this.scene = s;
    if (!set) {
      set = true;
      setAccentColor(AccentColor.primerPink());
      setFontSize(14);
      getScene().getRoot().getStylesheets().add(
              Objects.requireNonNull(Main.class.getResource("/com/r/projektnizad/styles/fontstyle.css")).toExternalForm()
      );

      setFontFamily("SF Pro Text Regular");
    }
    reloadCustomCSS();
  }

  public String getFontFamily() {
    return fontFamily;
  }

  public ThemeManager() {
  }


  public Scene getScene() {
    return scene;
  }

  private void setCustomDeclaration(String property, String value) {
    customCSSDeclarations.put(property, value);
  }

  private void removeCustomDeclaration(String property) {
    customCSSDeclarations.remove(property);
  }

  private void setCustomRule(String selector, String rule) {
    customCSSRules.put(selector, rule);
  }

  @SuppressWarnings("unused")
  private void removeCustomRule(String selector) {
    customCSSRules.remove(selector);
  }

  public void setFontFamily(String fontFamily) {
    Objects.requireNonNull(fontFamily);
    setCustomDeclaration("-fx-font-family", "\"" + fontFamily + "\"");

    this.fontFamily = fontFamily;
    reloadCustomCSS();
  }

  private void reloadCustomCSS() {
    Objects.requireNonNull(scene);
    StringBuilder css = new StringBuilder();

    css.append(".root:");
    css.append(USER_CUSTOM.getPseudoClassName());
    css.append(" {\n");
    customCSSDeclarations.forEach((k, v) -> {
      css.append("\t");
      css.append(k);
      css.append(": ");
      css.append(v);
      css.append(";\n");
    });
    css.append("}\n");

    customCSSRules.forEach((k, v) -> {
      // custom CSS is applied to the body,
      // thus it has a preference over accent color
      css.append(".body:");
      css.append(USER_CUSTOM.getPseudoClassName());
      css.append(" ");
      css.append(k);
      css.append(" {");
      css.append(v);
      css.append("}\n");
    });


    getScene().getRoot().getStylesheets().removeIf(uri -> uri.startsWith("data:text/css"));
    getScene().getRoot().getStylesheets().add(
            "data:text/css;base64," + Base64.getEncoder().encodeToString(css.toString().getBytes(UTF_8))
    );
    getScene().getRoot().pseudoClassStateChanged(USER_CUSTOM, true);
  }
}
