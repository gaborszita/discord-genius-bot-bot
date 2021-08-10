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

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

class UserAudioRecorder implements AudioReceiveHandler
{
   private static final int DEFAULT_ARRAY_RECORDED_LENGTH = 1000/20;
   ArrayList<byte[]> recordedAudio;
   User user;

   UserAudioRecorder(User user)
   {
      this(user, DEFAULT_ARRAY_RECORDED_LENGTH);
   }

   UserAudioRecorder(User user, int recordedLengthMilliSeconds)
   {
      this.user = user;
      recordedAudio = new ArrayList<>(recordedLengthMilliSeconds);
   }

   @Override
   public boolean canReceiveCombined()
   {
      return false;
   }

   @Override
   public boolean canReceiveUser()
   {
      return true;
   }

   @Override
   public void handleUserAudio(UserAudio userAudio)
   {
      double volume = 1.0;
      if(userAudio.getUser().equals(user))
         recordedAudio.add(userAudio.getAudioData(volume));
   }
}
