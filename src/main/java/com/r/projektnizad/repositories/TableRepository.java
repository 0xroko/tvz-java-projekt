/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.repositories;

import com.r.projektnizad.db.Database;
import com.r.projektnizad.exceptions.DatabaseActionFailException;
import com.r.projektnizad.models.Dao;
import com.r.projektnizad.models.Table;
import com.r.projektnizad.util.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
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
  public List<Table> getAll() throws DatabaseActionFailException {
    ArrayList<Table> tables = new ArrayList<>();
    try (Connection connection = Database.connect()) {
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery("SELECT * FROM `table`");
      while (rs.next()) {
        tables.add(mapToTable(rs));
      }
    } catch (Exception e) {
      logger.error("Error while fetching tables", e);
      throw new DatabaseActionFailException("Greška prilikom dohvata stolova.");
    }
    return tables;
  }

  private Table mapToTable(ResultSet rs) throws SQLException {
    return new Table(rs.getLong("id"), rs.getString("name"), rs.getString("description"), rs.getLong("seats"));
  }

  @Override
  public void save(Table table) throws DatabaseActionFailException {
    try (Connection connection = Database.connect()) {
      PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `table` (name, description, seats) VALUES (?, ?, ?)");
      preparedStatement.setString(1, table.getName());
      preparedStatement.setString(2, table.getDescription());
      preparedStatement.setLong(3, table.getSeats());
      preparedStatement.executeUpdate();
    } catch (Exception e) {
      logger.error("Error while saving table", e);
      throw new DatabaseActionFailException("Greška prilikom spremanja stola.");
    }

  }

  @Override
  public void update(Long id, Table table) throws DatabaseActionFailException {
    try (Connection connection = Database.connect()) {
      PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `table` SET name = ?, description = ?, seats = ? WHERE id = ?");
      preparedStatement.setString(1, table.getName());
      preparedStatement.setString(2, table.getDescription());
      preparedStatement.setLong(3, table.getSeats());
      preparedStatement.setLong(4, id);
      preparedStatement.executeUpdate();
    } catch (Exception e) {
      logger.error("Error while updating table", e);
      throw new DatabaseActionFailException("Greška prilikom ažuriranja stola.");
    }
  }

  @Override
  public void delete(Long id) throws DatabaseActionFailException {
    try (Connection connection = Database.connect()) {
      PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM `table` WHERE id = ?");
      preparedStatement.setLong(1, id);
      preparedStatement.executeUpdate();
    } catch (Exception e) {
      logger.error("Error while deleting table", e);
      throw new DatabaseActionFailException("Greška prilikom brisanja stola.");
    }
  }

  @Override
  public List<Table> filter(Map<String, Filter.FilterItem> filters) throws DatabaseActionFailException {
    ArrayList<Table> tables = new ArrayList<>();
    try (Connection connection = Database.connect()) {
      Statement statement = connection.createStatement();
      String query = "SELECT * FROM `table`";
      query = Filter.build(query, filters);
      logger.info("Query: " + query);
      ResultSet rs = statement.executeQuery(query);
      while (rs.next()) {
        tables.add(mapToTable(rs));
      }
    } catch (Exception e) {
      logger.error("Error while fetching tables", e);
      throw new DatabaseActionFailException("Greška prilikom dohvata stolova.");
    }
    return tables;
  }
}
