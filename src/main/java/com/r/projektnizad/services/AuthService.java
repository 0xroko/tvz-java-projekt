package com.r.projektnizad.services;

import com.r.projektnizad.enums.UserType;
import com.r.projektnizad.exceptions.InvalidPasswordException;
import com.r.projektnizad.exceptions.UserNotFoundException;
import com.r.projektnizad.exceptions.UsersNotLoadedError;
import com.r.projektnizad.models.User;
import com.r.projektnizad.models.change.AddChange;
import com.r.projektnizad.models.change.DeleteChange;
import com.r.projektnizad.models.change.ModifyChange;
import com.r.projektnizad.threads.ChangeWriterThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.r.projektnizad.util.Config.BASE_DATA_PATH;

/**
 * Load list of user from file and provide methods for authentication.
 */
public class AuthService {
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  static final private String USER_FILE = BASE_DATA_PATH + "users.txt";
  private final Set<User> users = new HashSet<>();
  private Optional<User> currentUser;

  public AuthService() {
    // load user from file
    File file = new File(USER_FILE);
    // if file does not exist, create it
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (Exception e) {
        logger.error("Error while creating file: " + e.getMessage());
      }
    }
    try {
      loadUsers();
    } catch (IOException e) {
      throw new UsersNotLoadedError("Error while loading users: " + e.getMessage(), e);
    }

    // add default admin user
    if (users.isEmpty()) {
      register("admin", AppPropertiesService.get("DEFAULT_ADMIN_PASSWORD"), UserType.ADMIN);
    }
    // add [deleted] user if not exist

    dev();
  }

  private void dev() {
    logger.info("Running in dev mode");
    this.currentUser = users.stream().filter(u -> u.getUsername().equals("admin")).findFirst();
  }

  public Optional<User> getCurrentUser() {
    return currentUser;
  }

  private Long nextUserId() {
    return users.stream().map(User::getId).max(Long::compareTo).orElse(0L) + 1;
  }

  public void register(String username, String password, UserType userType) {
    Optional<User> user = User.hashPassword(password).map(userPassword -> new User(nextUserId(), username, userPassword, userType));
    user.ifPresent(users::add);
    user.ifPresent(u -> new ChangeWriterThread<>(new AddChange<>(u.clone())).start());
    try {
      saveUsers();
    } catch (IOException e) {
      logger.error("Error while saving user: " + e.getMessage());
    }
  }

  public boolean authenticate(String username, String password) throws InvalidPasswordException, UserNotFoundException {
    Optional<User> user = users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    if (user.isPresent()) {
      if (User.verifyPassword(password, user.get().getPassword())) {
        currentUser = user;
        return currentUser.isPresent();
      } else {
        throw new InvalidPasswordException("Pogrešna lozinka");
      }
    } else {
      throw new UserNotFoundException("Korisnik nije pronađen");
    }
  }

  public void logout() {
    currentUser = Optional.empty();
  }

  private void saveUsers() throws IOException {
    Files.write(Path.of(USER_FILE), users.stream().map(User::toString).toList());
  }

  public Optional<User> getUserById(Long id) {
    return users.stream().filter(u -> u.getId().equals(id)).findFirst();
  }

  private void loadUsers() throws IOException {
    users.clear();
    // load
    for (String line : Files.readAllLines(Path.of(USER_FILE), StandardCharsets.UTF_8)) {
      String[] parts = line.split(";");
      if (parts.length == 4) {
        users.add(User.fromString(line));
      }
    }
  }

  public Set<User> getUsers() {
    try {
      loadUsers();
    } catch (IOException e) {
      logger.error("Error while loading user: " + e.getMessage());
    }
    return users.stream().filter(u -> !u.getUserType().equals(UserType.DELETE)).collect(Collectors.toSet());
  }

  public void deleteUser(User user) {
    users.remove(user);
    new ChangeWriterThread<>(new DeleteChange<>(user)).start();
    try {
      saveUsers();
    } catch (IOException e) {
      logger.error("Error while saving user: " + e.getMessage());
    }
  }

  public boolean updatePassword(Long userId, String password) {
    AtomicBoolean updated = new AtomicBoolean(false);
    users.stream().filter(u -> u.getId().equals(userId)).findFirst().ifPresent(u -> {
      u.setPassword(User.hashPassword(password).orElse(u.getPassword()));
      try {
        saveUsers();
        updated.set(true);
      } catch (IOException e) {
        logger.error("Error while saving user: " + e.getMessage());
      }
    });

    return updated.get();
  }

  public void editUser(User user) {
    Optional<User> oldUser = users.stream().filter(u -> u.getId().equals(user.getId())).findFirst();
    oldUser.ifPresent(value -> new ChangeWriterThread<>(new ModifyChange<>(value.clone(), user.clone())).start());
    users.removeIf(u -> u.getId().equals(user.getId()));
    users.add(user);

    try {
      saveUsers();
    } catch (IOException e) {
      logger.error("Error while saving user: " + e.getMessage());
    }
  }
}
