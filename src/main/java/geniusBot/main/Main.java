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

package geniusBot.main;

import javax.security.auth.login.LoginException;

import geniusBot.Keys;
import geniusBot.greeting.Greeting;
import geniusBot.counting.CountingWithMySQL;
import geniusBot.offensiveWordsPatrol.OffensiveWordsPatrol;
import geniusBot.musicUtilities.MusicCore;
import geniusBot.help.UserHelping;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;

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

      jda.addEventListener(new Greeting());
      jda.addEventListener(new CountingWithMySQL());
      jda.addEventListener(new OffensiveWordsPatrol());
      jda.addEventListener(new MusicCore());
      jda.addEventListener(new UserHelping());
   }
}

