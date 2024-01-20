module com.r.projektnizad {
  requires javafx.controls;
  requires javafx.fxml;

  requires org.slf4j;
  requires com.h2database;
  requires java.sql;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;

  opens com.r.projektnizad.main to javafx.fxml;
  exports com.r.projektnizad.main;
  opens com.r.projektnizad.controllers to javafx.fxml;
  exports com.r.projektnizad.controllers;
}