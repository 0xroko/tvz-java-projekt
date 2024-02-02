package com.r.projektnizad.threads;

import com.r.projektnizad.models.Entity;
import com.r.projektnizad.models.change.Change;
import com.r.projektnizad.services.HistoryChangeService;

public class ChangeWriterThread<T extends Entity> extends Thread {
  Change<T> change;

  public ChangeWriterThread(Change<T> change) {
    super();
    this.change = change;
  }

  @Override
  public void run() {
    super.run();
    HistoryChangeService.getInstance().writeChange(change);
  }
}
