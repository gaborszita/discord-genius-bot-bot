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

package geniusBot.greeting;

import geniusBot.mySQLutils.MySQLConnection;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Greeting extends ListenerAdapter
{
   public static final String SQL_GREETING_GUILD_DATA_TABLE =
           "greeting_guild_data";

   @Override
   public void onGuildMessageReceived(GuildMessageReceivedEvent event)
   {
      if(!event.getAuthor().isBot())
      {
         Message msg = event.getMessage();
         String msgData = (" " + msg.getContentRaw() + " ").toLowerCase();
         if (msgData.contains(" hi ") || msgData.contains(" hello "))
         {
            long guildId = event.getGuild().getIdLong();
            if (checkGuildGreetingEnabled(guildId))
            {
               MessageChannel channel = event.getChannel();
               channel.sendMessage("Hello, <@" + event.getAuthor().getId() +
                       ">").queue();
            }
         }
         if (msgData.contains(" what's up ") || msgData.contains(" wassup "))
         {
            long guildId = event.getGuild().getIdLong();
            if (checkGuildGreetingEnabled(guildId)) {
               MessageChannel channel = event.getChannel();
               channel.sendMessage("I'm doing great, <@" + event.getAuthor().getId() +
                       ">").queue();
            }
         }
      }
   }

   public static boolean checkGuildGreetingEnabled(long guildId)
   {
      try
      {
         Connection con = MySQLConnection.getConnection();
         Statement statement = con.createStatement();
         ResultSet resultSet = statement.executeQuery("SELECT * FROM "
                 + SQL_GREETING_GUILD_DATA_TABLE + " WHERE guildId = \""
                 + guildId + "\";");

         boolean enabled;
         if (resultSet.next())
            enabled = true;
         else
            enabled = false;

         con.close();
         return enabled;
      }
      catch (SQLException e)
      {
         e.printStackTrace();
         return false;
      }
   }
}
