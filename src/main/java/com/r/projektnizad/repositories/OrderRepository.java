/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.repositories;

import com.r.projektnizad.db.Database;
import com.r.projektnizad.enums.ItemOnOrderStatus;
import com.r.projektnizad.enums.OrderStatus;
import com.r.projektnizad.exceptions.DatabaseActionFailException;
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

  public List<ItemOnOrder> getItemsOnOrder(long orderId) throws DatabaseActionFailException {
    ArrayList<ItemOnOrder> items = new ArrayList<>();
    try (Connection conn = Database.connect()) {
      Statement stmt = conn.createStatement();
      String query = """
                             SELECT * FROM item_on_order JOIN item i on i.id = item_on_order.item_id
                             JOIN category c on c.id = i.category_id
                             WHERE item_on_order.order_id =
                             """ + " " + orderId + ";";
      logger.info(query);

      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        items.add(mapToItemOnOrder(rs));
      }
    } catch (SQLException e) {
      logger.error("Error getting items on order", e);
      throw new DatabaseActionFailException("Greška prilikom dohvata stavki narudžbe.");
    }
    return items;
  }

  public void deleteItemOnOrder(long id, Order o) throws DatabaseActionFailException {
    try (Connection conn = Database.connect()) {
      PreparedStatement stmt = conn.prepareStatement("DELETE FROM item_on_order WHERE id = ?");
      stmt.setLong(1, id);
      stmt.executeUpdate();
      o.getItemsOnOrder().removeIf(itemOnOrder -> itemOnOrder.getId() == id);
    } catch (SQLException e) {
      logger.error("Error while deleting item on order", e);
      throw new DatabaseActionFailException("Greška prilikom brisanja stavke narudžbe.");
    }
  }

  public void updateItemOnOrder(long id, ItemOnOrder itemOnOrder) throws DatabaseActionFailException {
    try (Connection conn = Database.connect()) {
      PreparedStatement stmt = conn.prepareStatement("UPDATE item_on_order SET item_id = ?, start_time = ?, status = ? WHERE id = ?");
      stmt.setLong(1, itemOnOrder.getItem().getId());
      stmt.setTimestamp(2, Timestamp.valueOf(itemOnOrder.getOrderTime()));
      stmt.setLong(3, itemOnOrder.getStatus().getCode());
      stmt.setLong(4, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error while updating item on order", e);
      throw new DatabaseActionFailException("Greška prilikom ažuriranja stavke narudžbe.");
    }
  }

  public void saveItemOnOrder(Order o, ItemOnOrder itemOnOrder) throws DatabaseActionFailException {
    try (Connection conn = Database.connect()) {
      PreparedStatement stmt = conn.prepareStatement("INSERT INTO item_on_order (item_id, start_time, status, order_id) VALUES (?, ?, ?, ?)", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
      stmt.setLong(1, itemOnOrder.getItem().getId());
      stmt.setTimestamp(2, Timestamp.valueOf(itemOnOrder.getOrderTime()));
      stmt.setLong(3, itemOnOrder.getStatus().getCode());
      stmt.setLong(4, o.getId());
      stmt.executeUpdate();
      ResultSet rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        itemOnOrder.setId(rs.getLong(1));
      }
      o.getItemsOnOrder().add(itemOnOrder);
    } catch (SQLException e) {
      logger.error("Error while saving item on order", e);
      throw new DatabaseActionFailException("Greška prilikom dodavanja stavke narudžbe.");
    }
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
  public List<Order> getAll() throws DatabaseActionFailException {
    ArrayList<Order> orders = new ArrayList<>();
    try (Connection conn = Database.connect()) {
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM `order` JOIN `table` t ON t.id = `order`.table_id;");
      while (rs.next()) {
        orders.add(mapToOrder(rs));
      }
    } catch (SQLException e) {
      logger.error("Error getting all orders", e);
      throw new DatabaseActionFailException("Greška prilikom dohvata narudžbi.");
    }
    return orders;
  }

  private Order mapToOrder(ResultSet rs) throws SQLException {
    Order o = new Order(
            rs.getLong("id"),
            new ArrayList<>(),
            new Table(rs.getLong("table_id"), rs.getString("t.name"), rs.getString("t.description"), rs.getLong("t.seats")),
            OrderStatus.fromCode(rs.getLong("status")),
            rs.getLong("user_id"),
            rs.getTimestamp("order_time").toLocalDateTime(),
            rs.getString("note")
    );

    if (rs.getLong("count") > 0) {
      o.setOrderItemsCount(rs.getLong("count"));
    }
    if (rs.getBigDecimal("total") != null) {
      o.setOrderPriceSum(rs.getBigDecimal("total"));
    }
    if (rs.getString("items") != null) {
      o.setOrderItemsString(rs.getString("items"));
    }
    return o;
  }

  @Override
  public void save(Order order) throws DatabaseActionFailException {
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
      throw new DatabaseActionFailException("Greška prilikom dodavanja narudžbe.");
    }
  }

  @Override
  public void update(Long id, Order order) throws DatabaseActionFailException {
    try (Connection conn = Database.connect()) {
      var stmt = conn.prepareStatement("UPDATE `order` SET table_id = ?, status = ?, user_id = ?, order_time = ?, note = ? WHERE id = ?");
      setOrder(order, stmt);
      stmt.setLong(6, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error while updating order", e);
      throw new DatabaseActionFailException("Greška prilikom ažuriranja narudžbe.");
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
  public void delete(Long id) throws DatabaseActionFailException {
    try (Connection conn = Database.connect()) {
      var stmt = conn.prepareStatement("DELETE FROM `order` WHERE id = ?");
      stmt.setLong(1, id);
      stmt.executeUpdate();
    } catch (SQLException e) {
      logger.error("Error while deleting order", e);
      throw new DatabaseActionFailException("Greška prilikom brisanja narudžbe.");
    }
  }

  @Override
  public List<Order> filter(Map<String, Filter.FilterItem> filters) throws DatabaseActionFailException {
    ArrayList<Order> orders = new ArrayList<>();
    try (Connection conn = Database.connect()) {
      Statement stmt = conn.createStatement();
      String query = """
              SELECT `order`.*, COALESCE(SUM(i.price), 0) AS total, COALESCE(CONCAT(GROUP_CONCAT(i.name SEPARATOR ', ' LIMIT 3 ), '...'), '') AS items, COUNT(i.name) AS count, t.*
              FROM `order`
                  LEFT JOIN `table` t ON t.id = `order`.table_id
                  LEFT JOIN `item_on_order` oi ON oi.order_id = `order`.id
                  LEFT JOIN `item` i ON i.id = oi.item_id
              """;
      query = Filter.build(query, filters);
      query += " GROUP BY `order`.id";
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        orders.add(mapToOrder(rs));
      }
      return orders;
    } catch (SQLException e) {
      logger.error("Error filtering orders", e);
      throw new DatabaseActionFailException("Greška prilikom filtriranja narudžbi.");
    }
  }
}
