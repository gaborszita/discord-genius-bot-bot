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

package geniusBot.moderating;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Moderating extends ListenerAdapter
{
   public static final String[] BAD_WORDS = {"fuck", "bitch", "shit"};

   @Override
   public void onGuildMessageReceived(GuildMessageReceivedEvent event)
   {
      Message msg = event.getMessage();
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
}