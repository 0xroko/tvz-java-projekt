package com.r.projektnizad.threads;

import javafx.concurrent.Task;

import java.util.function.Function;

public class PeriodicUpdaterTask<T, Fn extends Function<?, T>> extends Task<T> {
  private final Fn fn;
  private final Long period;

  public PeriodicUpdaterTask(Fn fn, Long period) {
    this.fn = fn;
    this.period = period;
  }

  @Override
  protected T call() {
    while (!Thread.currentThread().isInterrupted()) {
      updateValue(fn.apply(null));

      try {
        Thread.sleep(period);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }

    return getValue();
  }
}
