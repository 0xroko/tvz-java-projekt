package com.r.projektnizad.services;

import com.r.projektnizad.util.Navigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class AppPropertiesService {
  private static final Logger logger = LoggerFactory.getLogger(AppPropertiesService.class);
  private static final String APP_PROPERTIES_FILE = "app.properties";
  public static Properties properties = new Properties();

  static public String get(String key) {
    return properties.getProperty(key);
  }

  static public void load() {
    try {
      properties.load(AppPropertiesService.class.getResourceAsStream(Navigator.baseResourcePath + APP_PROPERTIES_FILE));
    } catch (IOException e) {
      logger.error("[EXIT] Error while loading properties: " + e.getMessage(), e);
      System.exit(1);
    }
  }

}
