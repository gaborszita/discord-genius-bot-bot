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
