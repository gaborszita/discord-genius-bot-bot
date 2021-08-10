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

package geniusBot.musicRecognizer;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MusicRecognizerManager
{
   final ArrayList<MusicRecognizerHolder> musicRecognizers = new
           ArrayList<>();

   public MusicRecognizer createMusicRecognizer(@NotNull AudioManager audioManager,
                                                @NotNull MusicRecognizerResultHandler
                                             resultHandler, @NotNull JDA jda)
   {
      MusicRecognizer musicRecognizer = new MusicRecognizer (audioManager,
              resultHandler, jda, this);
      jda.addEventListener(musicRecognizer);
      Thread thread = new Thread(musicRecognizer);
      thread.start();
      musicRecognizers.add(new MusicRecognizerHolder(musicRecognizer, thread));
      return musicRecognizer;
   }

   public void destroy()
   {
      for (MusicRecognizerHolder recognizer : musicRecognizers)
      {
         recognizer.musicRecognizer.destroy();
      }
   }
}
