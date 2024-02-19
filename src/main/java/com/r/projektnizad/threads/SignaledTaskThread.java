package com.r.projektnizad.threads;

import com.r.projektnizad.models.ObservableThreadTask;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

public class SignaledTaskThread<T, P> {
  private final Object lock = new Object();
  private boolean taskPending = false;
  private final Thread taskThread;
  private final ObservableThreadTask<T, P> task = new ObservableThreadTask<>();

  public SignaledTaskThread(Function<P, T> fn) {
    task.setFn(fn);
    // set thread name
    taskThread = Thread.ofVirtual().start(() -> {
      while (!Thread.currentThread().isInterrupted()) {
        synchronized (lock) {
          while (!taskPending) {
            try {
              lock.wait();
            } catch (InterruptedException e) {
              Thread.currentThread().interrupt();
              return;
            }
          }
          task.call();
          taskPending = false;
          lock.notify();
        }
      }
    });
  }

  public ReadOnlyObjectProperty<T> getResultProperty() {
    return task.getResultProperty();
  }

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

  public void interrupt() {
    taskThread.interrupt();
  }
}
