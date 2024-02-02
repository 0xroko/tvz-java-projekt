/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.dao;

import com.r.projektnizad.db.Database;
import com.r.projektnizad.enums.OrderStatus;
import com.r.projektnizad.models.*;
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

public class OrderDao implements Dao<Order> {

  private static final Logger logger = LoggerFactory.getLogger(OrderDao.class);

  @Override
  public Optional<Order> get(long id) {
    return Optional.empty();
  }

  public List<ItemOnOrder> getItemsOnOrder(long orderId) {
    ArrayList<ItemOnOrder> items = new ArrayList<>();
    try (Connection conn = Database.connect()) {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM `item_on_order` JOIN `item` i2 on i2.'id' = 'item_on_order'.'item_id' " +
              "JOIN category c on c.id = i2.id" +
              " WHERE 'item_on_order'.'order_id' = " + orderId);
      while (rs.next()) {
        items.add(mapToItemOnOrder(rs));
      }
    } catch (SQLException e) {
      logger.error("Error getting items on order", e);
    }
    return items;
  }

  private ItemOnOrder mapToItemOnOrder(ResultSet rs) throws SQLException {
    return new ItemOnOrder(
            rs.getLong("id"),
            new ItemBuilder()
                    .setId(rs.getLong("i2.id"))
                    .setName(rs.getString("i2.name"))
                    .setDescription(rs.getString("i2.description"))
                    .setPrice(rs.getBigDecimal("i2.price"))
                    .setCategory(new Category(rs.getLong("c.id"), rs.getString("c.name"), rs.getString("c.description")))
                    .createItem(),

            rs.getTimestamp("order_time").toLocalDateTime(),
            null
    );
  }

  @Override
  public List<Order> getAll() {
    ArrayList<Order> orders = new ArrayList<>();
    try (Connection conn = Database.connect()) {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM `order` JOIN `table` t ON t.id = `order`.table_id;");
      while (rs.next()) {
        orders.add(mapToOrder(rs));
      }
    } catch (SQLException e) {
      logger.error("Error getting all orders", e);
    }
    return orders;
  }

  private Order mapToOrder(ResultSet rs) throws SQLException {
    return new Order(
            rs.getLong("id"),
            new ArrayList<>(),
            new Table(rs.getLong("table_id"), rs.getString("t.name"), rs.getString("t.description"), rs.getLong("t.seats")),
            OrderStatus.fromCode(rs.getLong("status")),
            rs.getLong("user_id"),
            rs.getTimestamp("order_time").toLocalDateTime(),
            rs.getString("note")
    );
  }

  @Override
  public void save(Order order) {

  }

  @Override
  public void update(Long id, Order order) {

  }

  @Override
  public void delete(Long id) {

  }

  @Override
  public List<Order> filter(Map<String, Filter.FilterItem> filters) {
    ArrayList<Order> orders = new ArrayList<>();
    try (Connection conn = Database.connect()) {
      Statement stmt = conn.createStatement();
      String query = "SELECT * FROM `order` JOIN `table` t ON t.id = `order`.table_id";
      query = Filter.build(query, filters);
      logger.info(query);
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        orders.add(mapToOrder(rs));
      }
      return orders;
    } catch (SQLException e) {
      logger.error("Error filtering orders", e);
    }
    return orders;
  }
}
