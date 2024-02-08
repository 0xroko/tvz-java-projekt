package com.r.projektnizad.threads;

import com.r.projektnizad.models.ObservableThreadTask;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public class SignaledTaskThread<T, P> extends Thread {
  private static final Logger logger = LoggerFactory.getLogger(SignaledTaskThread.class);
  private final Object lock = new Object();
  private boolean taskPending = false;
  private final ObservableThreadTask<T, P> task = new ObservableThreadTask<>();

  public SignaledTaskThread(Function<P, T> fn) {
    task.setFn(fn);
    setDaemon(false);
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
  }

  public void signal() {
    synchronized (lock) {
      taskPending = true;
      lock.notifyAll();
    }
  }

  public void signal(P param) {
    synchronized (lock) {
      task.setParam(param);
      taskPending = true;
      lock.notifyAll();
    }
  }
}
