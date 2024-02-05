package com.r.projektnizad.services;

import com.r.projektnizad.exceptions.PropertiesNotLoadedError;
import com.r.projektnizad.util.Navigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class AppPropertiesService {
  private static final Logger logger = LoggerFactory.getLogger(AppPropertiesService.class);
  private static final String APP_PROPERTIES_FILE = "app.properties";
  public static Properties properties = new Properties();

  static public String get(String key) {
    if (properties.isEmpty()) {
      logger.info("Key: " + key);
      throw new PropertiesNotLoadedError("Properties file not loaded");
    }
    return properties.getProperty(key);
  }

  static public void load() {
    try {
      properties.load(AppPropertiesService.class.getResourceAsStream(Navigator.baseResourcePath + APP_PROPERTIES_FILE));
    } catch (IOException e) {
      throw new PropertiesNotLoadedError("Error while loading properties file: " + e.getMessage(), e);
    }
  }

}
