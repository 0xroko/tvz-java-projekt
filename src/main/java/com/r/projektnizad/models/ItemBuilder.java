package com.r.projektnizad.models;

import com.r.projektnizad.enums.ItemType;

import java.math.BigDecimal;
import java.time.Duration;

public class ItemBuilder {
  private String name;
  private String description;
  private BigDecimal price;
  private Category category;
  private Long stock;
  private Long defaultStockIncrement = 1L;
  private ItemType itemType;
  private Duration preparationTime = Duration.ZERO;
  private Long id;

  public ItemBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public ItemBuilder setDescription(String description) {
    this.description = description;
    return this;
  }

  public ItemBuilder setPrice(BigDecimal price) {
    this.price = price;
    return this;
  }

  public ItemBuilder setCategory(Category category) {
    this.category = category;
    return this;
  }

  public ItemBuilder setStock(Long stock) {
    this.stock = stock;
    return this;
  }

  public ItemBuilder setDefaultStockIncrement(Long defaultStockIncrement) {
    this.defaultStockIncrement = defaultStockIncrement;
    return this;
  }

  public ItemBuilder setItemType(ItemType itemType) {
    this.itemType = itemType;
    return this;
  }

  public ItemBuilder setPreparationTime(Duration preparationTime) {
    this.preparationTime = preparationTime;
    return this;
  }

  public ItemBuilder setId(Long id) {
    this.id = id;
    return this;
  }

  public Item createItem() {
    return new Item(id, name, description, price, category, stock, defaultStockIncrement, itemType, preparationTime);
  }
}