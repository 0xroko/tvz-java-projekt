package com.r.projektnizad.models;

import com.r.projektnizad.util.Filter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Dao<T extends Entity> {

  Optional<T> get(long id);

  List<T> getAll();

  void save(T t);

  void update(Long id, T t);

  void delete(Long id);

  List<T> filter(Map<String, Filter.FilterItem> filters);
}

