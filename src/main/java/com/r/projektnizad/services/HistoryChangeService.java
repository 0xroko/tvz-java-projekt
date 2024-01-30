package com.r.projektnizad.services;

import com.r.projektnizad.exceptions.HistoryEntryNotFound;
import com.r.projektnizad.models.Entity;
import com.r.projektnizad.models.history.Change;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.r.projektnizad.util.Config.BASE_DATA_PATH;

public class HistoryChangeService {
  static final Logger logger = LoggerFactory.getLogger(HistoryChangeService.class);

  static final String HISTORY_DIRECTORY = BASE_DATA_PATH + "history";

  static final String rotatedHistoryFileFormat = "history_%s.dat";
  ReadWriteLock lock;

  public HistoryChangeService() {
    super();
    lock = new ReentrantReadWriteLock();
    init();
  }

  public void init() {
    try {
      Files.createDirectories(Path.of(HISTORY_DIRECTORY));
    } catch (IOException e) {
      logger.error("Error creating history directory", e);
    }
  }

  public Path getHistoryFile(Date date) {
    String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
    String fileName = String.format(rotatedHistoryFileFormat, dateStr);
    return Path.of(HISTORY_DIRECTORY, fileName);
  }

  private <T extends Entity> ArrayList<Change<T>> read(ObjectInputStream in) throws IOException, ClassNotFoundException {
    Object obj = in.readObject();
    if (obj instanceof ArrayList) {
      return (ArrayList<Change<T>>) obj;
    }
    return new ArrayList<>();
  }

  public <T extends Entity> Optional<ArrayList<Change<T>>> readChanges(Date date) throws HistoryEntryNotFound {
    Path historyFile = getHistoryFile(date);
    lock.readLock().lock();
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(historyFile.toFile()))) {
      return Optional.of(read(ois));
    } catch (FileNotFoundException e) {
      logger.info("History file not found");
      throw new HistoryEntryNotFound();
    } catch (IOException | ClassNotFoundException e) {
      logger.error("Error reading history file", e);
    } finally {
      lock.readLock().unlock();
    }
    return Optional.empty();
  }

  public <T extends Entity> void writeChange(Change<T> change) {
    Path historyFile = getHistoryFile(new Date());
    lock.writeLock().lock();

    ArrayList<Change<T>> changes = new ArrayList<>();
    // read changes from history file
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(historyFile.toFile()))) {
      changes = read(ois);
    } catch (FileNotFoundException e) {
      logger.info("History file not found");
    } catch (IOException e) {
      logger.error("Error reading history file", e);
    } catch (ClassNotFoundException e) {
      logger.error("Looks like history file is corrupted", e);
    }

    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(historyFile.toFile(), false))) {
      changes.add(change);
      oos.writeObject(changes);
    } catch (IOException | ClassCastException e) {
      logger.error("Error writing history file", e);
    } finally {
      lock.writeLock().unlock();
    }
  }

}
