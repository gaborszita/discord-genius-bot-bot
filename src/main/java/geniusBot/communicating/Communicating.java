package geniusBot.communicating;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Communicating extends ListenerAdapter
{
   @Override
   public void onGuildMessageReceived(GuildMessageReceivedEvent event)
   {
      if(!event.getAuthor().isBot())
      {
         Message msg = event.getMessage();
         String msgData = (" " + msg.getContentRaw() + " ").toLowerCase();
         if (msgData.contains(" hi ") || msgData.contains(" hello "))
         {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Hello, <@" + event.getAuthor().getId() + 
                  ">").queue();;
         }
         if (msgData.contains(" what's up ") || msgData.contains(" wassup "))
         {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("I'm doing great, <@" + event.getAuthor().getId() + 
                  ">").queue();;
         }
      }
   }
}
