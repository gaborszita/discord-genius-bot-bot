package geniusBot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config
{
   private static final Properties prop;

   static
   {
      prop = new Properties();
      String fileName = "botconfig.txt";
      try (FileInputStream fis = new FileInputStream(fileName))
      {
         prop.load(fis);
      }
      catch (FileNotFoundException e)
      {
         System.out.println("Config file doesn't exist. Cannot read config.");
      }
      catch (IOException e)
      {
         System.out.println("Failed to read config file.");
         e.printStackTrace();
      }
   }

   public static String getConfig(String key)
   {
      String val = prop.getProperty(key);
      if (val != null)
      {
         return val;
      }
      else
      {
         String errormsg = "Could not read key requested in config! This is " +
                 "most likely due to a bad config file, please check config " +
                 "file! Requested key: " + key;
         throw new RuntimeException(errormsg);
      }
   }
}
