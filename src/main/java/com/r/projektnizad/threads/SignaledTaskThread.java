package com.r.projektnizad.threads;

import com.r.projektnizad.models.ObservableThreadTask;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class SignaledTaskThread<T, P> extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(SignaledTaskThread.class);
  private final Object lock = new Object();
  private boolean taskPending = false;
  private final ObservableThreadTask<T, P> task = new ObservableThreadTask<>();
  private Long period = 0L;

  public SignaledTaskThread(Function<P, T> fn) {
    task.setFn(fn);
    // set thread name
    this.setName("SignaledTaskThread");
    this.start();
  }

  public SignaledTaskThread(Function<P, T> fn, Long period) {
    task.setFn(fn);
    this.period = period;
    // set thread name
    this.setName("SignaledTaskThread");
    this.start();
  }

  public ReadOnlyObjectProperty<T> getResultProperty() {
    return task.getResultProperty();
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        while (!taskPending) {
          synchronized (lock) {
            lock.wait();
          }
        }
        task.call();
        taskPending = false;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    logger.info("Thread ended");
  }

  /**
   * Calling this will keep the old params
   */
  public void signal() {
    synchronized (lock) {
      taskPending = true;
      lock.notify();
    }
  }

  public void signal(P param) {
    synchronized (lock) {
      task.setParam(param);
      taskPending = true;
      lock.notify();
    }
  }
}
