/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.repositories;

import com.r.projektnizad.db.Database;
import com.r.projektnizad.enums.ItemOnOrderStatus;
import com.r.projektnizad.enums.OrderStatus;
import com.r.projektnizad.models.*;
import com.r.projektnizad.util.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OrderRepository implements Dao<Order> {

  private static final Logger logger = LoggerFactory.getLogger(OrderRepository.class);

  @Override
  public Optional<Order> get(long id) {
    return Optional.empty();
  }

  public List<ItemOnOrder> getItemsOnOrder(long orderId) {
    ArrayList<ItemOnOrder> items = new ArrayList<>();
    try (Connection conn = Database.connect()) {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM `item_on_order` JOIN `item` i on i.'id' = 'item_on_order'.'item_id' " +
              "JOIN category c on c.id = i.id" +
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
                    .setId(rs.getLong("i.id"))
                    .setName(rs.getString("i.name"))
                    .setDescription(rs.getString("i.description"))
                    .setPrice(rs.getBigDecimal("i.price"))
                    .setCategory(new Category(rs.getLong("c.id"), rs.getString("c.name"), rs.getString("c.description")))
                    .createItem(),
            rs.getTimestamp("start_time").toLocalDateTime(),
            ItemOnOrderStatus.fromCode(rs.getLong("status"))
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
    try (Connection conn = Database.connect()) {
      var stmt = conn.prepareStatement("INSERT INTO `order` (table_id, status, user_id, order_time, note) VALUES (?, ?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
      setOrder(order, stmt);
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        order.setId(rs.getLong(1));
      }
    } catch (SQLException e) {
      logger.error("Error while saving order", e);
    }
  }

  @Override
  public void update(Long id, Order order) {
    try (Connection conn = Database.connect()) {
      var stmt = conn.prepareStatement("UPDATE `order` SET table_id = ?, status = ?, user_id = ?, order_time = ?, note = ? WHERE id = ?");
      setOrder(order, stmt);
      stmt.setLong(6, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error while updating order", e);
    }
  }

  private void setOrder(Order order, PreparedStatement stmt) throws SQLException {
    stmt.setLong(1, order.getTable().getId());
    stmt.setLong(2, order.getStatus().getCode());
    stmt.setLong(3, order.getUserId());
    stmt.setTimestamp(4, Timestamp.valueOf(order.getOrderTime()));
    stmt.setString(5, order.getNote());
  }

  @Override
  public void delete(Long id) {
    try (Connection conn = Database.connect()) {
      var stmt = conn.prepareStatement("DELETE FROM `order` WHERE id = ?");
      stmt.setLong(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error while deleting order", e);
    }
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
