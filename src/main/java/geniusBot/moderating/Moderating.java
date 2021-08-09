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