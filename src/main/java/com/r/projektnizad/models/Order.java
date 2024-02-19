package com.r.projektnizad.models;

import com.r.projektnizad.annotations.NamedHistoryMember;
import com.r.projektnizad.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Order extends Entity implements Serializable, Cloneable {
  @Serial
  private static final long serialVersionUID = 1L;
  @NamedHistoryMember("Stavke")
  private ArrayList<ItemOnOrder> itemsOnOrder;
  @NamedHistoryMember("Stol")
  private Table table;
  @NamedHistoryMember("Status")
  private OrderStatus status;
  @NamedHistoryMember("Korisnik")
  private Long userId;
  @NamedHistoryMember("Detalji")
  private String note;
  /**
   * If RESERVED, this is the time when the order time is beginning.
   */
  @NamedHistoryMember("Datum i vrijeme")
  private LocalDateTime orderTime;
  private BigDecimal orderPriceSum = BigDecimal.ZERO;
  private Long orderItemsCount = 0L;
  private String orderItemsString = "";

  public Order(Long id, ArrayList<ItemOnOrder> itemsOnOrder, Table table, OrderStatus status, Long userId, LocalDateTime orderTime, String note) {
    super(id);
    this.itemsOnOrder = itemsOnOrder;
    this.table = table;
    this.status = status;
    this.userId = userId;
    this.orderTime = orderTime;
    this.note = note;
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

  public LocalDateTime getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(LocalDateTime orderTime) {
    this.orderTime = orderTime;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Override
  public String getEntityName() {
    return "narudžba";
  }


  public BigDecimal getOrderPriceSum() {
    return orderPriceSum;
  }

  public void setOrderPriceSum(BigDecimal orderPriceSum) {
    this.orderPriceSum = orderPriceSum;
  }

  public Long getOrderItemsCount() {
    return orderItemsCount;
  }

  public void setOrderItemsCount(Long orderItemsCount) {
    this.orderItemsCount = orderItemsCount;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getOrderItemsString() {
    return orderItemsString;
  }

  public void setOrderItemsString(String orderItemsString) {
    this.orderItemsString = orderItemsString;
  }

  @Override
  public Order clone() {
    try {
      Order clone = (Order) super.clone();
      clone.itemsOnOrder = new ArrayList<>();
      for (ItemOnOrder itemOnOrder : itemsOnOrder) {
        clone.itemsOnOrder.add(itemOnOrder.clone());
      }

      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
