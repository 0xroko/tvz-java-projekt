module com.r.projektnizad {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.slf4j;
  requires com.h2database;
  requires java.sql;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;

  requires atlantafx.base;
  requires atlantafx.styles;

  requires javafx.base;
  requires javafx.graphics;
  requires org.kordamp.ikonli.core;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.ikonli.feather;

  opens com.r.projektnizad.main to javafx.fxml;
  opens com.r.projektnizad.controllers.category to javafx.fxml;
  opens com.r.projektnizad.controllers to javafx.fxml;

  exports com.r.projektnizad.main;
  exports com.r.projektnizad.models;
  exports com.r.projektnizad.services;
  exports com.r.projektnizad.models.history;
  exports com.r.projektnizad.util;
  exports com.r.projektnizad.controllers;
  exports com.r.projektnizad.enums;
  exports com.r.projektnizad.exceptions;
}