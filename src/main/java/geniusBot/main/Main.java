package geniusBot.main;

import javax.security.auth.login.LoginException;

import geniusBot.Keys;
import geniusBot.communicating.Communicating;
import geniusBot.counting.CountingWithMySQL;
import geniusBot.moderating.Moderating;
import geniusBot.musicUtilities.MusicCore;
import help.UserHelping;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Main 
{
   public static void main(String[] args)
   {
      JDA jda;
      JDABuilder builder = JDABuilder.createDefault(Keys.DISCORD_TOKEN);

      try
      {
        jda = builder.build();
      }
      catch (LoginException e)
      {
         System.out.println("Bot login faliure");
         return;
         //System.out.println(e);
      }
      jda.getPresence().setActivity(Activity.playing("g!help | " +
              "geniusbot.gaborszita.net"));

      jda.addEventListener(new Communicating());
      jda.addEventListener(new CountingWithMySQL());
      jda.addEventListener(new Moderating());
      jda.addEventListener(new MusicCore());
      jda.addEventListener(new UserHelping());
   }
}

