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

package geniusBot.help;

import java.sql.*;
import geniusBot.commandUtils.*;
import geniusBot.mySQLutils.MySQLConnection;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserHelping extends ListenerAdapter
{
   private static final String helpMessage = 
         "These are the available help commands:\n";

   MySQLConnection sqlConnection;
   public static final String SQL_HELP_DATA_TABLE = "help_commands";
   public static final String HELP_COMMAND = "help";

   public UserHelping()
   {
      sqlConnection = new MySQLConnection();
   }

   @Override
   public void onGuildMessageReceived(GuildMessageReceivedEvent event)
   {  
      String message = event.getMessage().getContentRaw();
      if(Command.isMessageCommand(message))
      {
         String[] commands;
         try
         {
            commands = Command.getCommandParts(message);
         } 
         catch (IllegalArgumentException e)
         {
            e.printStackTrace();
            return;
         }
         if(commands.length==1 && commands[0].equals(HELP_COMMAND))
         {
            try
            {
               StringBuilder sendMessage = new StringBuilder(helpMessage.
                     length());
               sendMessage.append(helpMessage);
               Connection con;
               con = MySQLConnection.getConnection();
               Statement statement = con.createStatement();
               ResultSet resultSet = statement.executeQuery("SELECT * FROM "
                     + SQL_HELP_DATA_TABLE + ";");
               while(resultSet.next())
               {
                  sendMessage.append("g!help " + resultSet.getString(1) + " - " 
                        + resultSet.getString(2) + "\n");
               }
               event.getChannel().sendMessage(sendMessage).queue();
            }
            catch(SQLException e)
            {
               e.printStackTrace();
            }
         }
         else if(commands.length==2 && commands[0].equals(HELP_COMMAND))
         {
            try
            {
               StringBuilder sendMessage = new StringBuilder(helpMessage.
                     length());
               sendMessage.append(helpMessage);
               Connection con;
               con = MySQLConnection.getConnection();
               Statement statement = con.createStatement();
               ResultSet resultSet = statement.executeQuery("SELECT * FROM "
                     + SQL_HELP_DATA_TABLE + " WHERE command = \"" + commands[1]
                           + "\";");
               if(resultSet.next())
               {
                  sendMessage.append(resultSet.getString(3));
               }
               event.getChannel().sendMessage(sendMessage).queue();
            }
            catch(SQLException e)
            {
               e.printStackTrace();
            }
         }
      }
   }
}
