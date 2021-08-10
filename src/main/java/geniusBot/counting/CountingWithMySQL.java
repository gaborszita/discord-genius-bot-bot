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

package geniusBot.counting;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.*;

import geniusBot.mySQLutils.MySQLConnection;

public class CountingWithMySQL extends ListenerAdapter
{
   public static final String COUNT_CHANNEL = "count";
   public static final int COUNT_STEP = 1;

   public static final String SQL_COUNTER_GUILD_DATA_TABLE = 
         "counter_guild_data";

   public static class GuildData
   {
      public long nextCount;
      public long lastCountMessageId;
      public long lastCountSenderId;
      public boolean botMessageSent;

      GuildData()
      {
         nextCount = 1;
      }
   }

   private GuildData getGuildDataFromEvent(GenericGuildEvent event)
   {
      GuildData guildData = new GuildData();
      long searchGuildId = event.getGuild().getIdLong();
      
      try
      {
         Connection con;
         con = MySQLConnection.getConnection();
         Statement statement = con.createStatement();
         ResultSet resultSet = statement.executeQuery("SELECT * FROM "
               + SQL_COUNTER_GUILD_DATA_TABLE + " WHERE guildId = \"" 
               + searchGuildId + "\";");

         if(resultSet.next())
         {
            guildData.nextCount = resultSet.getLong(2);
            guildData.lastCountMessageId = resultSet.getLong(3);
            guildData.lastCountSenderId = resultSet.getLong(4);
            guildData.botMessageSent = resultSet.getBoolean(5);
         }
         else
         {
            statement.executeUpdate("INSERT INTO "
                  + SQL_COUNTER_GUILD_DATA_TABLE
                  + "(guildId, nextCount, lastCountMessageId, "
                  + "lastCountSenderId, botMessageSent) VALUES(\"" + 
                  searchGuildId + "\",\"" + guildData.nextCount + "\",\"" + 
                  guildData.lastCountMessageId + "\",\"" + 
                  guildData.lastCountSenderId + "\",\"" + 
                  (guildData.botMessageSent ? 1 : 0)  + "\");");
         }
      } 
      catch (SQLException e)
      {
         e.printStackTrace();
         return null;
      }
      return guildData;
   }

   private boolean removeGuildDataFromEvent(GenericGuildEvent event)
   {
      long searchGuildId = event.getGuild().getIdLong();

      try
      {
         Connection con;
         con = MySQLConnection.getConnection();
         Statement statement = con.createStatement();

         int rowsAffected = statement.executeUpdate("DELETE FROM " + 
               SQL_COUNTER_GUILD_DATA_TABLE + " WHERE guildId = \"" +
               searchGuildId + "\";");

         if(rowsAffected>0)
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
         return false;
      }
   }

   private boolean updateGuildDataOnSQLDatabaseFromEvent(
         GenericGuildEvent event, GuildData guildData)
   {
      long searchGuildId = event.getGuild().getIdLong();

      try
      {
         Connection con;
         con = MySQLConnection.getConnection();
         Statement statement = con.createStatement();
         int rowsAffected = statement.executeUpdate("UPDATE " + 
               SQL_COUNTER_GUILD_DATA_TABLE + " SET nextCount = \"" + 
               guildData.nextCount + "\", lastCountMessageId = \"" + 
               guildData.lastCountMessageId + "\", lastCountSenderId = \"" + 
               guildData.lastCountSenderId + "\", botMessageSent = \"" + 
               (guildData.botMessageSent ? 1 : 0 )+ "\" WHERE " + 
               "guildId = \"" + searchGuildId + "\";");

         if(rowsAffected>0)
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      catch(SQLException e)
      {
         e.printStackTrace();
         return false;
      }
   }
   
   private long getCounterChannelIdFromEvent(GenericGuildEvent event)
   {
      try
      {
         Connection con;
         con = MySQLConnection.getConnection();
         Statement statement = con.createStatement();
         ResultSet resultSet = statement.executeQuery("SELECT counterChannelId "
               + "FROM " + SQL_COUNTER_GUILD_DATA_TABLE + " WHERE guildId = "
                     + "\"" + event.getGuild().getId() + "\";");

         if(resultSet.next())
         {
            return resultSet.getLong(1);
         }
         else
         {
            return 0;
         }
      }
      catch(SQLException e)
      {
         e.printStackTrace();
         return 0;
      }
   }

   @Override
   public void onGuildMessageReceived(GuildMessageReceivedEvent event)
   {
      long channelId = getCounterChannelIdFromEvent(event);
      MessageChannel channel = event.getChannel();
      if (channel.getIdLong() == channelId)
      {
         GuildData guildData = getGuildDataFromEvent(event);
         Message msg = event.getMessage();
         if(!guildData.botMessageSent)
         {
            if(!msg.getContentRaw().equals("" + guildData.nextCount))
            {
               msg.delete().queue();
            }
            else
            {
               guildData.lastCountMessageId = msg.getIdLong();
               guildData.lastCountSenderId = msg.getAuthor().getIdLong();
               guildData.nextCount++;
            }
         }
         else
         {
            guildData.botMessageSent = false;
         }
         updateGuildDataOnSQLDatabaseFromEvent(event, guildData);
      }
   }

   @Override
   public void onGuildMessageDelete(GuildMessageDeleteEvent event)
   {
      GuildData guildData = getGuildDataFromEvent(event);
      MessageChannel channel = event.getChannel();
      if (event.getMessageIdLong()==guildData.lastCountMessageId)
      {
         guildData.botMessageSent = true;
         channel.sendMessage(guildData.nextCount-COUNT_STEP + " (<@" + 
               guildData.lastCountSenderId + ">)").queue();
      }
      updateGuildDataOnSQLDatabaseFromEvent(event, guildData);
   }

   @Override
   public void onGuildLeave(GuildLeaveEvent event)
   {
      removeGuildDataFromEvent(event);
   }
}