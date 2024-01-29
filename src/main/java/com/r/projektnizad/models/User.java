package com.r.projektnizad.models;

import com.r.projektnizad.enums.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.Serializable;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Optional;

public class User extends Entity implements Serializable {
  static Logger logger = LoggerFactory.getLogger(User.class);
  private String username;
  private UserPassword password;
  private UserType userType;

  @Override
  public String getEntityName() {
    return "korisnik";
  }


  public String getUsername() {
    return username;
  }

  public UserPassword getPassword() {
    return password;
  }

  public User(Long id, String username, UserPassword password, UserType userType) {
    super(id);
    this.username = username;
    this.password = password;
    this.userType = userType;
  }

  static public Optional<UserPassword> hashPassword(String password) {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);

    try {
      KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] hash = factory.generateSecret(spec).getEncoded();
      return Optional.of(new UserPassword(hash, salt));
    } catch (Exception e) {
      logger.error("Error while hashing password: " + e.getMessage());
    }
    return Optional.empty();
  }

  static public boolean verifyPassword(String password, UserPassword userPassword) {
    try {
      KeySpec spec = new PBEKeySpec(password.toCharArray(), userPassword.salt(), 65536, 128);
      SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
      byte[] hash = factory.generateSecret(spec).getEncoded();
      return Arrays.equals(hash, userPassword.hash());
    } catch (Exception e) {
      logger.error("Error while verifying password: " + e.getMessage());
    }
    return false;
  }

  @Override
  public String toString() {
    return getId() + ";" + username + ";" + password + ";" + userType.getCode();
  }

  public static User fromString(String str) {
    String[] parts = str.split(";");
    return new User(Long.parseLong(parts[0]), parts[1], UserPassword.fromString(parts[2]), UserType.fromCode(Integer.parseInt(parts[3])));
  }
}
