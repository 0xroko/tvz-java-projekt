package com.r.projektnizad.threads;

import com.r.projektnizad.main.Main;
import com.r.projektnizad.models.Entity;
import com.r.projektnizad.models.history.Change;

public class ChangeWriterThread<T extends Entity> extends Thread {
  Change<T> change;

  public ChangeWriterThread(Change<T> change) {
    super();
    this.change = change;
  }

  @Override
  public void run() {
    super.run();
    Main.historyChangeService.writeChange(change);
  }
}
