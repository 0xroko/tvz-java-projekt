package com.r.projektnizad.models;

import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.function.Function;

public class ObservableThreadTask<ResultValue, Params> {
  private final ReadOnlyObjectWrapper<ResultValue> resultProperty;
  private Function<Params, ResultValue> fn;
  private Params param;

  public ObservableThreadTask() {
    resultProperty = new ReadOnlyObjectWrapper<>();
  }

  public ReadOnlyObjectWrapper<ResultValue> getResultProperty() {
    return resultProperty;
  }

  public void setFn(Function<Params, ResultValue> fn) {
    this.fn = fn;
  }

  public Params getParam() {
    return param;
  }

  public void setParam(Params param) {
    this.param = param;
  }

  public void call() {
    resultProperty.set(fn.apply(param));
  }
}
