/*
 * Copyright 2020-2021 Gabor Szita
 *
 * This file is part of Discord Genius Bot.
 *
 * Discord Genius Bot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Discord Genius Bot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Discord Genius Bot.  If not, see <https://www.gnu.org/licenses/>.
 */

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
