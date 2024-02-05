package com.r.projektnizad.util;

import com.r.projektnizad.exceptions.DatabaseActionFailException;

@FunctionalInterface
public interface CheckedFunction<T, R> {
  R apply(T t) throws DatabaseActionFailException;
}