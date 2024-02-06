package com.r.projektnizad.models;

import com.r.projektnizad.enums.ItemOnOrderStatus;
import com.r.projektnizad.models.change.ChangeAccessor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ItemOnOrder extends Entity implements Serializable, Cloneable, ChangeAccessor {
  @Serial
  private static final long serialVersionUID = 1L;
  private Item item;
  private LocalDateTime orderTime;
  private ItemOnOrderStatus status;

  public ItemOnOrder(Long id, Item item, LocalDateTime orderTime, ItemOnOrderStatus status) {
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

  public LocalDateTime getOrderTime() {
    return orderTime;
  }

  public void setOrderTime(LocalDateTime orderTime) {
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

  @Override
  public ItemOnOrder clone() {
    try {
      ItemOnOrder clone = (ItemOnOrder) super.clone();
      clone.setItem(item.clone());
      return clone;
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

  @Override
  public String access() {
    return this.getItem().getName();
  }
}
