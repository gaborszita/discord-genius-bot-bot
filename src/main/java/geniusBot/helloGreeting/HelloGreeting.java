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
