package com.r.projektnizad.dao;

import com.r.projektnizad.models.Dao;
import com.r.projektnizad.models.Item;

import java.util.List;
import java.util.Optional;

public class ItemDao implements Dao<Item> {
  @Override
  public Optional<Item> get(long id) {
    return Optional.empty();
  }

  @Override
  public List<Item> getAll() {
    return null;
  }

  @Override
  public void save(Item item) {

  }

  @Override
  public void update(Long id, Item item) {

  }

  @Override
  public void delete(Long id) {

  }
}
