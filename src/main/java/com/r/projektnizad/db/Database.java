package com.r.projektnizad.db;

import com.r.projektnizad.services.AppPropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
  static private final Logger logger = LoggerFactory.getLogger(Database.class);

  public static Connection connect() throws SQLException {
    return DriverManager.getConnection(AppPropertiesService.get("DB_HOST"), AppPropertiesService.get("DB_USER"), AppPropertiesService.get("DB_PASSWORD"));
  }
}
