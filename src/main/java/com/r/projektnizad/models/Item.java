package com.r.projektnizad.models;

import com.r.projektnizad.decorators.NamedHistoryMember;
import com.r.projektnizad.enums.ItemType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;

public class Item extends Entity implements Serializable, Cloneable {
  @NamedHistoryMember("Naziv")
  private String name;
  @NamedHistoryMember("Opis")
  private String description;
  @NamedHistoryMember("Cijena")
  private BigDecimal price;
  @NamedHistoryMember("Kategorija")
  private Category category;
  @NamedHistoryMember("Zaliha")
  private Long stock;
  @NamedHistoryMember("Povećanje zalihe")
  private Long defaultStockIncrement = 1L;
  @NamedHistoryMember("Tip")
  private ItemType itemType;
  @NamedHistoryMember("Vrijeme pripreme")
  private Duration preparationTime;

  public Item(Long id, String name, String description, BigDecimal price, Category category, Long stock, Long defaultStockIncrement, ItemType itemType, Duration preparationTime) {
    super(id);
    this.name = name;
    this.description = description;
    this.price = price;
    this.category = category;
    this.stock = stock;
    this.itemType = itemType;
    this.preparationTime = preparationTime;
    this.defaultStockIncrement = defaultStockIncrement;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Long getStock() {
    return stock;
  }

  public void setStock(Long stock) {
    this.stock = stock;
  }

  public Long getDefaultStockIncrement() {
    return defaultStockIncrement;
  }

  public void setDefaultStockIncrement(Long defaultStockIncrement) {
    this.defaultStockIncrement = defaultStockIncrement;
  }

  public ItemType getItemType() {
    return itemType;
  }

  public void setItemType(ItemType itemType) {
    this.itemType = itemType;
  }

  public Duration getPreparationTime() {
    return preparationTime;
  }

  public void setPreparationTime(Duration preparationTime) {
    this.preparationTime = preparationTime;
  }

  @Override
  public String getEntityName() {
    return "artikal";
  }

  @Override
  public Item clone() {
    try {
      return (Item) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
