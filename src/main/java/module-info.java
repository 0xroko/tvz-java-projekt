module com.r.projektnizad {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.slf4j;
  requires com.h2database;
  requires java.sql;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;
  requires org.apache.commons.text;
  requires org.apache.commons.lang3;
  requires atlantafx.base;
  requires atlantafx.styles;

  requires javafx.base;
  requires javafx.graphics;
  requires org.kordamp.ikonli.core;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.ikonli.feather;

  opens com.r.projektnizad.main to javafx.fxml;
  opens com.r.projektnizad.controllers.category to javafx.fxml;
  opens com.r.projektnizad.controllers.item to javafx.fxml;
  opens com.r.projektnizad.controllers to javafx.fxml;
  opens com.r.projektnizad.controllers.order to javafx.fxml;
  opens com.r.projektnizad.controllers.user to javafx.fxml;
  opens com.r.projektnizad.util.controlfx to javafx.fxml;

  exports com.r.projektnizad.controllers.item;
  exports com.r.projektnizad.controllers.category;
  exports com.r.projektnizad.controllers.order;
  exports com.r.projektnizad.controllers.user;


  exports com.r.projektnizad.main;
  exports com.r.projektnizad.threads;
  exports com.r.projektnizad.models;
  exports com.r.projektnizad.services;
  exports com.r.projektnizad.models.change;
  exports com.r.projektnizad.util;
  exports com.r.projektnizad.controllers;
  exports com.r.projektnizad.enums;
  exports com.r.projektnizad.exceptions;
  exports com.r.projektnizad.util.controlfx;
}