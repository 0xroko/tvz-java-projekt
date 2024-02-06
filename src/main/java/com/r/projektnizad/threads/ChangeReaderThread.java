package com.r.projektnizad.threads;

import com.r.projektnizad.exceptions.HistoryEntryNotFoundException;
import com.r.projektnizad.models.Entity;
import com.r.projektnizad.models.ObservableThreadTask;
import com.r.projektnizad.models.change.Change;
import com.r.projektnizad.services.HistoryChangeService;
import javafx.beans.property.ReadOnlyObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;

public class ChangeReaderThread extends Thread {
  private final ObservableThreadTask<ArrayList<Change<Entity>>, Date> task = new ObservableThreadTask<>();
  private static final Logger logger = LoggerFactory.getLogger(ChangeReaderThread.class);
  private final HistoryChangeService historyChangeService = HistoryChangeService.getInstance();

  public ReadOnlyObjectProperty<ArrayList<Change<Entity>>> getResultProperty() {
    return task.getResultProperty();
  }

  public void updateParams(Date date) {
    task.setParam(date);
  }

  boolean forceRerun = false;

  public void updateParamsAndRerun(Date date) {
    forceRerun = true;
    task.setParam(date);
    this.interrupt();
  }

  public ChangeReaderThread() {
    super();
    task.setParam(new Date());
    task.setFn(param -> {
      try {
        return historyChangeService.readChanges(param).orElse(new ArrayList<>());
      } catch (HistoryEntryNotFoundException h) {
        return new ArrayList<>();
      }
    });
    // set thread name
    this.setName("ChangeReaderThread");
    this.start();
  }

  public void end() {
    this.interrupt();
  }

  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        task.call();
        logger.info("ChangeReaderThread finished");
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        if (forceRerun) {
          forceRerun = false;
          continue;
        }
        Thread.currentThread().interrupt();
      }
    }
  }
}
