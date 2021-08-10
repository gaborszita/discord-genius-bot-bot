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

package geniusBot.commandUtils;

import java.util.Arrays;

public class Command
{
   public static final String COMMAND_IDENTIFIER = "g!";

   public static boolean isMessageCommand(String message)
   {
      return message.toLowerCase().startsWith(COMMAND_IDENTIFIER);
   }

   public static String[] getCommandParts(String message) 
         throws IllegalArgumentException
   {
      message = message.toLowerCase();
      if(!isMessageCommand(message))
      {
         throw new IllegalArgumentException(message + " is not a command!");
      }
      //message = message.substring(COMMAND_IDENTIFIER.length());
      message = " " + message.substring(COMMAND_IDENTIFIER.length());
      String[] commands = message.split("\\s+");
      if(commands.length>0)
      {
         commands = Arrays.copyOfRange(commands, 1, commands.length);
      }
      return commands;
   }
}
