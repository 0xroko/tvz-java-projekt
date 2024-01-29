package com.r.projektnizad.services;

import com.r.projektnizad.enums.UserType;
import com.r.projektnizad.models.User;
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

import static com.r.projektnizad.util.Config.BASE_DATA_PATH;

/**
 * Load list of users from file and provide methods for authentication.
 */
public class AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  static final private String USER_FILE = BASE_DATA_PATH + "users.txt";

  private final Set<User> users = new HashSet<>();
  private Optional<User> currentUser;

  public AuthService() {
    // load users from file
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
      logger.error("[EXIT] Error while loading users: " + e.getMessage(), e);
      System.exit(1);
    }

    // add default admin user
    if (users.isEmpty()) {
      register("admin", AppPropertiesService.get("DEFAULT_ADMIN_PASSWORD"), UserType.ADMIN);
    }
    currentUser = users.stream().findFirst();
  }

  public Optional<User> getCurrentUser() {
    return currentUser;
  }

  private Long nextUserId() {
    return users.stream().map(User::getId).max(Long::compareTo).orElse(0L) + 1;
  }

  public Optional<User> register(String username, String password, UserType userType) {
    Optional<User> user = User.hashPassword(password).map(userPassword -> new User(nextUserId(), username, userPassword, userType));
    user.ifPresent(users::add);
    try {
      saveUsers();
    } catch (IOException e) {
      logger.error("Error while saving users: " + e.getMessage());
    }
    return user;
  }


  public boolean authenticate(String username, String password) {
    Optional<User> user = users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    if (user.isPresent()) {
      if (User.verifyPassword(password, user.get().getPassword())) {
        currentUser = user;
      }
    }
    return currentUser.isPresent();
  }

  public void logout() {
    currentUser = Optional.empty();
  }

  private void saveUsers() throws IOException {
    // save
    Files.write(Path.of(USER_FILE), users.stream().map(User::toString).toList());
  }

  private void loadUsers() throws IOException {
    users.clear();
    // load
    for (String line : Files.readAllLines(Path.of(USER_FILE), StandardCharsets.UTF_8)) {
      String[] parts = line.split(";");
      if (parts.length == 3) {
        users.add(User.fromString(line));
      }
    }
  }
}
