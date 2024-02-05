package com.r.projektnizad.models;

import com.r.projektnizad.exceptions.DatabaseActionFailException;
import com.r.projektnizad.util.Filter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Dao<T extends Entity> {

  Optional<T> get(long id) throws DatabaseActionFailException;

  List<T> getAll() throws DatabaseActionFailException;

  void save(T t) throws DatabaseActionFailException;

  void update(Long id, T t) throws DatabaseActionFailException;

  void delete(Long id) throws DatabaseActionFailException;

  List<T> filter(Map<String, Filter.FilterItem> filters) throws DatabaseActionFailException;
}

