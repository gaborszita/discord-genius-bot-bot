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

package geniusBot.mySQLutils;

import java.sql.*;

public class MySQLConnection
{
   private static boolean isConnected = false;
   private static Connection con;
   
   public static Connection getConnection() throws SQLException
   {
      if(isConnected)
      {
         return con;
      }
      else
      {
         Connect();
         return con;
      }
   }
   
   public static void Connect() throws SQLException
   {
      if(!isConnected)
      {
         con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"
               + "dbname?useSSL=false&serverTimezone=UTC", "dbusername",
               "dbpassword");
      }
      isConnected = true;
   }
   
   public static void Disconnect() throws SQLException
   {
      if(isConnected)
      {
         con.close();
      }
      isConnected = false;
   }
}
