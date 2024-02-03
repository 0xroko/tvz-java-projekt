package com.r.projektnizad.repositories;

import com.r.projektnizad.db.Database;
import com.r.projektnizad.enums.ItemType;
import com.r.projektnizad.models.Category;
import com.r.projektnizad.models.Dao;
import com.r.projektnizad.models.Item;
import com.r.projektnizad.models.ItemBuilder;
import com.r.projektnizad.util.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemRepository implements Dao<Item> {
  private static final Logger logger = LoggerFactory.getLogger(ItemRepository.class);

  @Override
  public Optional<Item> get(long id) {
    return Optional.empty();
  }

  @Override
  public List<Item> getAll() {
    ArrayList<Item> items = new ArrayList<>();
    try (Connection conn = Database.connect()) {
      ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM item JOIN category c on c.id = item.category_id");
      while (rs.next()) {
        var item = mapToItem(rs);
        items.add(item);
      }
    } catch (SQLException e) {
      logger.error("Error while fetching items", e);
    }
    return items;
  }

  @Override
  public void save(Item item) {
    try (Connection conn = Database.connect()) {
      var stmt = conn.prepareStatement("INSERT INTO item (name, description, price, category_id, stock, default_stock_increment, preparation_time, item_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
      setItem(item, stmt);
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        item.setId(rs.getLong(1));
      }
    } catch (SQLException e) {
      logger.error("Error while saving item", e);
    }

  }

  public Item mapToItem(ResultSet resultSet) throws SQLException {
    ItemBuilder itemBuilder = new ItemBuilder();

    itemBuilder.setId(resultSet.getLong("id"));
    itemBuilder.setName(resultSet.getString("name"));
    itemBuilder.setDescription(resultSet.getString("description"));
    itemBuilder.setPrice(resultSet.getBigDecimal("price"));
    itemBuilder.setStock(resultSet.getLong("stock"));
    itemBuilder.setDefaultStockIncrement(resultSet.getLong("default_stock_increment"));
    itemBuilder.setPreparationTime(Duration.between(LocalTime.MIDNIGHT, Optional.ofNullable(resultSet.getTime("preparation_time")).orElse(Time.valueOf(LocalTime.MIDNIGHT)).toLocalTime()));
    itemBuilder.setItemType(ItemType.fromCode(resultSet.getLong("item_type")));
    Category category = new Category(resultSet.getLong("category_id"), resultSet.getString("c.name"), resultSet.getString("c.description"));
    itemBuilder.setCategory(category);
    return itemBuilder.createItem();
  }

  @Override
  public void update(Long id, Item item) {
    try (Connection conn = Database.connect()) {
      var stmt = conn.prepareStatement("UPDATE item SET name = ?, description = ?, price = ?, category_id = ?, stock = ?, default_stock_increment = ?, preparation_time = ?, item_type = ? WHERE id = ?");
      setItem(item, stmt);
      stmt.setLong(9, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error while updating item", e);
    }

  }

  private void setItem(Item item, PreparedStatement stmt) throws SQLException {
    stmt.setString(1, item.getName());
    stmt.setString(2, item.getDescription());
    stmt.setBigDecimal(3, item.getPrice());
    stmt.setLong(4, item.getCategory().getId());
    stmt.setLong(5, item.getStock());
    stmt.setLong(6, item.getDefaultStockIncrement());
    stmt.setTime(7, Time.valueOf(LocalTime.MIDNIGHT.plus(item.getPreparationTime())));
    stmt.setLong(8, item.getItemType().getCode());
  }

  @Override
  public void delete(Long id) {
    try (Connection conn = Database.connect()) {
      var stmt = conn.prepareStatement("DELETE FROM item WHERE id = ?");
      stmt.setLong(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error while deleting item", e);
    }
  }

  @Override
  public List<Item> filter(Map<String, Filter.FilterItem> filters) {
    ArrayList<Item> items = new ArrayList<>();
    try (Connection conn = Database.connect()) {
      String query = "SELECT * FROM item JOIN category c on c.id = item.category_id";
      String filterQuery = Filter.build(query, filters);
      logger.info(filterQuery);
      ResultSet rs = conn.createStatement().executeQuery(filterQuery);
      while (rs.next()) {
        var item = mapToItem(rs);
        items.add(item);
      }
    } catch (SQLException e) {
      logger.error("Error while fetching items", e);
    }

    return items;
  }
}
