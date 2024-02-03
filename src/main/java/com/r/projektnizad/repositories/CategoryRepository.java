package com.r.projektnizad.repositories;

import com.r.projektnizad.db.Database;
import com.r.projektnizad.models.Category;
import com.r.projektnizad.models.Dao;
import com.r.projektnizad.util.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CategoryRepository implements Dao<Category> {
  private static final Logger logger = LoggerFactory.getLogger(CategoryRepository.class);

  @Override
  public Optional<Category> get(long id) {
    return Optional.empty();
  }

  @Override
  public List<Category> getAll() {
    List<Category> categories = new ArrayList<>();
    try (var conn = Database.connect()) {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM category");
      while (rs.next()) {
        var category = mapToCategory(rs);
        categories.add(category);
      }
    } catch (SQLException e) {
      logger.error("Error while fetching categories", e);
    }
    return categories;
  }

  private Category mapToCategory(ResultSet rs) throws SQLException {
    var id = rs.getLong("id");
    var name = rs.getString("name");
    var description = rs.getString("description");
    return new Category(id, name, description);
  }

  @Override
  public void save(Category category) {
    try (Connection conn = Database.connect()) {
      PreparedStatement stmt = conn.prepareStatement("INSERT INTO category (name, description) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, category.getName());
      stmt.setString(2, category.getDescription());
      stmt.executeUpdate();

      // get the id
      ResultSet rs = stmt.getGeneratedKeys();

      if (rs.next()) {
        category.setId(rs.getLong(1));
      }
    } catch (SQLException e) {
      logger.error("Error while saving category", e);
    }
  }

  @Override
  public void update(Long id, Category category) {
    try (Connection conn = Database.connect()) {
      PreparedStatement stmt = conn.prepareStatement("UPDATE category SET name = ?, description = ? WHERE id = ?");
      stmt.setString(1, category.getName());
      stmt.setString(2, category.getDescription());
      stmt.setLong(3, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error while updating category", e);
    }
  }

  @Override
  public void delete(Long id) {
    try (Connection conn = Database.connect()) {
      PreparedStatement stmt = conn.prepareStatement("DELETE FROM category WHERE id = ?");
      stmt.setLong(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error while deleting category", e);
    }

  }

  @Override
  public List<Category> filter(Map<String, Filter.FilterItem> filters) {
    return null;
  }
}
