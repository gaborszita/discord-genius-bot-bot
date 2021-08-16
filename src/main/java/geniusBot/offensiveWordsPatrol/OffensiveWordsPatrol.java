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

package geniusBot.offensiveWordsPatrol;

import geniusBot.mySQLutils.MySQLConnection;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.sql.rowset.RowSetWarning;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

public class OffensiveWordsPatrol extends ListenerAdapter
{
   public static final String[] BAD_WORDS = {"fuck", "bitch", "shit"};
   public static final String SQL_OFFENSIVE_WORDS_PATROL_GUILD_DATA_TALE =
           "offensive_words_patrol_guild_data";

   @Override
   public void onGuildMessageReceived(GuildMessageReceivedEvent event)
   {
      Message msg = event.getMessage();
      long guildId = msg.getGuild().getIdLong();
      boolean[] enabledData = checkGuildOffensiveWordsPatrolEnabled(guildId);
      // return immediately of offensive words patrol isn't enabled
      if (!enabledData[0] || (msg.getTextChannel().isNSFW() && !enabledData[1]))
         return;
      String msgData = msg.getContentRaw();
      msgData = msgData.toLowerCase();
      for(String str : BAD_WORDS)
      {
         if(msgData.indexOf(str) != -1)
         {
            msg.delete().queue();
            event.getAuthor().openPrivateChannel().flatMap(channel ->
            channel.sendMessage("<@" + event.getAuthor().getId() + "> " +
                  "Please don't be rude in this server! :angry:")).queue();
            break;
         }
      }
   }

   // Return boolean array: pos 0 identifies whether the patrol is enabled, and
   // pos 1 identifies if it is enabled on nsfw channels;
   public static boolean[] checkGuildOffensiveWordsPatrolEnabled(long guildId)
   {
      try
      {
         Connection con = MySQLConnection.getConnection();
         Statement statement = con.createStatement();
         ResultSet resultSet = statement.executeQuery("SELECT watchNSFW FROM "
                 + SQL_OFFENSIVE_WORDS_PATROL_GUILD_DATA_TALE + " WHERE guildId = \""
                 + guildId + "\";");

         boolean result[] = new boolean[2];
         if (resultSet.next())
         {
            result[0] = true;
            boolean watchNSFW = resultSet.getBoolean(1);
            result[1] = watchNSFW;
         }
         else
         {
            result[0] = false;
            result[1] = false;
         }

         con.close();
         return result;
      }
      catch (SQLException e)
      {
         e.printStackTrace();
         return new boolean[]{false, false};
      }
   }
}