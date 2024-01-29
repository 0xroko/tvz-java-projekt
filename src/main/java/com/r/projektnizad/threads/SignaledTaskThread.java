package com.r.projektnizad.threads;

import javafx.beans.property.ReadOnlyObjectWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class SignaledTaskThread<T> extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(SignaledTaskThread.class);
  private final Object lock = new Object();
  private final ReadOnlyObjectWrapper<T> resultProperty = new ReadOnlyObjectWrapper<>();
  private boolean taskPending = false;
  private final Function<Void, T> fn;

  public ReadOnlyObjectWrapper<T> resultProperty() {
    return resultProperty;
  }

  public SignaledTaskThread(Function<Void, T> fn) {
    this.fn = fn;
    // set thread name
    this.setName("SignaledTaskThread");
    this.start();
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
        resultProperty.set(fn.apply(null));
        taskPending = false;
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    logger.info("Thread ended");
  }

  public void signal() {
    synchronized (lock) {
      taskPending = true;
      lock.notify();
    }
  }
}
