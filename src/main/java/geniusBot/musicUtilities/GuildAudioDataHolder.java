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

package geniusBot.musicUtilities;

import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import geniusBot.musicUtilities.musicPlayer.AudioPlayerSendHandler;
import geniusBot.musicUtilities.musicPlayer.TrackScheduler;
import geniusBot.musicRecognizer.MusicRecognizer;
import geniusBot.musicRecognizer.MusicRecognizerManager;
import geniusBot.musicRecognizer.MusicRecognizerResultHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

public class GuildAudioDataHolder
{
   static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
   AudioManager audioManager;
   AudioPlayer player;
   TrackScheduler trackScheduler;
   volatile MusicRecognizer recognizer;
   long serverId;
   volatile TextChannel boundChannel;

   static
   {
      AudioSourceManagers.registerRemoteSources(playerManager);
   }

   GuildAudioDataHolder(GenericGuildEvent event)
   {
      audioManager = event.getGuild().getAudioManager();
      serverId = event.getGuild().getIdLong();
   }

   public TextChannel getBoundChannel()
   {
      return boundChannel;
   }

   public void playerBuild()
   {
      if(player == null || trackScheduler == null)
      {
         player = playerManager.createPlayer();
         audioManager.setSendingHandler(new AudioPlayerSendHandler(player));
         trackScheduler = new TrackScheduler(player, this);
         player.addListener(trackScheduler);
      }
   }

   public void playerSlightDestroy()
   {
      if(player!=null)
         player.destroy();
      player = null;
      trackScheduler = null;
   }

   public void destroy(@NotNull Map<Long, GuildAudioDataHolder> guildAudioData)
   {
      playerSlightDestroy();
      if(audioManager!=null)
      {
         audioManager.setSendingHandler(null);
         audioManager.closeAudioConnection();
      }
      audioManager = null;
      guildAudioData.remove(serverId);
   }

   public void safeDisconnect()
   {
      if(audioManager!=null)
         audioManager.closeAudioConnection();
   }
}
