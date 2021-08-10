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

package geniusBot.helloGreeting;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class HelloGreeting extends ListenerAdapter
{
   @Override
   public void onGuildMessageReceived(GuildMessageReceivedEvent event)
   {
      Message msg = event.getMessage();
      String msgData = (" " + msg.getContentRaw() + " ").toLowerCase();
      if (msgData.contains(" hi ") || msgData.contains(" hello "))
      {
         MessageChannel channel = event.getChannel();
         channel.sendMessage("Hello, <@" + event.getAuthor().getId() + 
               ">").queue();;
      }
   }
}
