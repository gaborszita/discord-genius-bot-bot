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
