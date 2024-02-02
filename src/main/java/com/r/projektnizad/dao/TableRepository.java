/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.dao;

import com.r.projektnizad.db.Database;
import com.r.projektnizad.models.Dao;
import com.r.projektnizad.models.Table;
import com.r.projektnizad.util.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TableRepository implements Dao<Table> {
  private static final Logger logger = LoggerFactory.getLogger(TableRepository.class);

  @Override
  public Optional<Table> get(long id) {
    return Optional.empty();
  }

  @Override
  public List<Table> getAll() {
    ArrayList<Table> tables = new ArrayList<>();
    try (Connection connection = Database.connect()) {
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery("SELECT * FROM `table`");
      while (rs.next()) {
        tables.add(map(rs));
      }
    } catch (Exception e) {
      logger.error("Error while fetching tables", e);
    }
    return tables;
  }

  private Table map(ResultSet rs) throws SQLException {
    return new Table(rs.getLong("id"), rs.getString("name"), rs.getString("description"), rs.getLong("seats"));
  }

  @Override
  public void save(Table table) {

  }

  @Override
  public void update(Long id, Table table) {

  }

  @Override
  public void delete(Long id) {

  }

  @Override
  public List<Table> filter(Map<String, Filter.FilterItem> filters) {
    return null;
  }
}
