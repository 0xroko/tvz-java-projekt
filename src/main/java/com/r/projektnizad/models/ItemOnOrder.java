package com.r.projektnizad.models;

import com.r.projektnizad.enums.ItemOnOrderStatus;

import java.io.Serializable;
import java.util.Date;

public class ItemOnOrder extends Entity implements Serializable {
  private Item item;
  private Date orderTime;
  private ItemOnOrderStatus status;

  public ItemOnOrder(Long id, Item item, Date orderTime, ItemOnOrderStatus status) {
    super(id);
    this.item = item;
    this.orderTime = orderTime;
    this.status = status;
  }

  public Item getItem() {
    return item;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public Date getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(Date orderTime) {
    this.orderTime = orderTime;
  }

  public ItemOnOrderStatus getStatus() {
    return status;
  }

  public void setStatus(ItemOnOrderStatus status) {
    this.status = status;
  }

  @Override
  public String getEntityName() {
    return "stavka narudžbe";
  }
}
