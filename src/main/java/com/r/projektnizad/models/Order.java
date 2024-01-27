package com.r.projektnizad.models;

import com.r.projektnizad.enums.OrderStatus;

import java.util.ArrayList;
import java.util.Date;

public class Order extends Entity {
  private ArrayList<ItemOnOrder> itemsOnOrder;
  private Table table;
  private OrderStatus status;
  /**
   * If RESERVED, this is the time when the order time is beginning.
   */
  private Date orderTime;

  public Order(Long id, ArrayList<ItemOnOrder> itemsOnOrder, Table table, OrderStatus status, Date orderTime) {
    super(id);
    this.itemsOnOrder = itemsOnOrder;
    this.table = table;
    this.status = status;
    this.orderTime = orderTime;
  }

  public ArrayList<ItemOnOrder> getItemsOnOrder() {
    return itemsOnOrder;
  }

  public void setItemsOnOrder(ArrayList<ItemOnOrder> itemsOnOrder) {
    this.itemsOnOrder = itemsOnOrder;
  }

  public Table getTable() {
    return table;
  }

  public void setTable(Table table) {
    this.table = table;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public Date getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(Date orderTime) {
    this.orderTime = orderTime;
  }
}
