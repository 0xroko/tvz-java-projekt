package com.r.projektnizad.models;

import com.r.projektnizad.decorators.NamedHistoryMember;
import com.r.projektnizad.enums.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.Serializable;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class User extends Entity implements Serializable, Cloneable {
  static Logger logger = LoggerFactory.getLogger(User.class);
  @NamedHistoryMember("Korisničko ime")
  private String username;
  private UserPassword password;
  @NamedHistoryMember("Tip korisnika")
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
      byte[] userHash = userPassword.hash();
      return Arrays.equals(hash, userHash);
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

  public void setUsername(String username) {
    this.username = username;
  }

  public UserType getUserType() {
    return userType;
  }

  public void setUserType(UserType userType) {
    this.userType = userType;
  }

  public String getUsernameWithType() {
    return username + " (" + userType.getName() + ")";
  }

  public void setPassword(UserPassword password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(username, user.username) && Objects.equals(password, user.password) && userType == user.userType;
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password, userType);
  }

  @Override
  public User clone() {
    try {
      return (User) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
