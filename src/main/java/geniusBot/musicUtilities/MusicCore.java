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

import geniusBot.musicUtilities.musicPlayer.MyAudioLoadResultHandler;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.*;

import java.util.LinkedHashMap;
import java.util.Map;

import geniusBot.commandUtils.Command;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MusicCore extends ListenerAdapter
{
   
   private final Map<Long, GuildAudioDataHolder> guildAudioData;
   private static final int MUSIC_RECOGNIZER_SECONDS_TIMEOUT = 15;

   public MusicCore()
   {
      guildAudioData = new LinkedHashMap<>();
   }
   
   private GuildAudioDataHolder 
      getGuildAudioDataHolderFromEvent(GenericGuildEvent event)
   {
      long serverId = event.getGuild().getIdLong();
      GuildAudioDataHolder guildAudioDataHolder = guildAudioData.get(serverId);
      if(guildAudioDataHolder == null)
      {
         guildAudioDataHolder = new GuildAudioDataHolder(event);
         guildAudioData.put(serverId, guildAudioDataHolder);
      }
      return guildAudioDataHolder;
   }
   
   @Override
   public void onGuildMessageReceived(GuildMessageReceivedEvent event)
   {
      Message msg = event.getMessage();
      String messageData = msg.getContentRaw();

      if(Command.isMessageCommand(messageData))
      {
         String[] commands;
         try
         {
            commands = Command.getCommandParts(messageData);
         } 
         catch (IllegalArgumentException e)
         {
            e.printStackTrace();
            return;
         }
         GuildAudioDataHolder audioDataHolder = 
               getGuildAudioDataHolderFromEvent(event);

         boolean boundChannelChanged = false;
         if(!event.getChannel().equals(audioDataHolder.boundChannel))
         {
            audioDataHolder.boundChannel = event.getChannel();
            boundChannelChanged = true;
         }

         try
         {
            if(commands.length == 1 && commands[0].equals("connect") &&
                  connect(audioDataHolder, event))
            {
               audioDataHolder.boundChannel.sendMessage("Connected").queue();
            }
            else if(commands.length == 1 && commands[0].equals("disconnect"))
            {
               audioDataHolder.safeDisconnect();
            }
            else if(commands.length >= 2 && commands[0].equals("play") &&
                  connect(audioDataHolder, event))
            {
               StringBuilder allCommands = new StringBuilder();
               for(int i=1; i<commands.length; i++)
               {
                  allCommands.append(commands[i]).append(" ");
               }

               audioDataHolder.playerBuild();
               GuildAudioDataHolder.playerManager.loadItem(commands[1], new
                       MyAudioLoadResultHandler(audioDataHolder.boundChannel,
                       audioDataHolder.trackScheduler,
                       GuildAudioDataHolder.playerManager,
                           allCommands.toString()));
            }
            else if(commands.length == 1 && commands[0].equals("stop") &&
                  connect(audioDataHolder, event))
            {
               audioDataHolder.player.stopTrack();
            }
            else if(commands.length == 1 && commands[0].equals("pause") &&
                  connect(audioDataHolder, event))
            {
               audioDataHolder.player.setPaused(true);
            }
            else if(commands.length == 1 && commands[0].equals("resume") &&
                  connect(audioDataHolder, event))
            {
               audioDataHolder.player.setPaused(false);
            }
            else if(commands.length == 1 && commands[0].equals("next") &&
                  connect(audioDataHolder, event))
            {
               if(audioDataHolder.trackScheduler == null ||
                       !audioDataHolder.trackScheduler.next())
               {
                  if(audioDataHolder.player != null)
                     audioDataHolder.player.stopTrack();
                  audioDataHolder.boundChannel.sendMessage
                          (":grey_exclamation: No tracks in queue! " +
                                  "Stopped playing. :grey_exclamation: ").queue();
               }
               else
                  audioDataHolder.boundChannel.sendMessage("Next track started").queue();
            }
            if(boundChannelChanged)
            {
               audioDataHolder.boundChannel.sendMessage("Bound to channel "
                       + audioDataHolder.boundChannel.getName()).queue();
            }
         }
         catch (VoiceConnectException e)
         {
            event.getChannel().sendMessage("Couldn't connect to your " +
                    "voice channel.").queue();
            e.printStackTrace();
         }
      }
   }

   @Override
   public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event)
   {
      if(event.getMember().getIdLong() ==
              event.getJDA().getSelfUser().getIdLong())
      {
         GuildAudioDataHolder audioDataHolder = guildAudioData.get(event.
                 getGuild().getIdLong());
         if(audioDataHolder != null)
            audioDataHolder.destroy(guildAudioData);
      }
   }

   private static boolean connect(GuildAudioDataHolder audioDataHolder,
                                  GuildMessageReceivedEvent event)
           throws VoiceConnectException
   {
      GuildVoiceState memberVoiceState;
      if(event.getMember()!=null && event.getMember().getVoiceState()!=null)
      {
         memberVoiceState = event.getMember().getVoiceState();
      }
      else if(event.getMember()==null)
      {
         throw new VoiceConnectException("The message received was sent vi" +
                 "a webhook!");
      }
      else if(event.getMember().getVoiceState()==null)
      {
         throw new VoiceConnectException("CacheFlag.VOICE_STATE was disabled" +
                 " manually!");
      }
      else
      {
         throw new VoiceConnectException();
      }

      VoiceChannel botChannel = audioDataHolder.audioManager.
            getConnectedChannel();
      VoiceChannel memberChannel = memberVoiceState.getChannel();
      long botChannelId = botChannel == null ? 0 : botChannel.getIdLong();
      long memberChannelId = memberChannel == null ? 0 : 
         memberChannel.getIdLong();
      if(memberChannelId!=0 && memberChannelId!=botChannelId)
      {
         audioDataHolder.playerBuild();
         audioDataHolder.player.stopTrack();
         VoiceChannel voiceChannel = memberVoiceState.getChannel();
         audioDataHolder.audioManager.openAudioConnection(voiceChannel);
         return true;
      }
      else if(memberChannelId != 0)
      {
         return true;
      }
      else
      {
         event.getChannel().sendMessage("Please connect to a voice channel!").
            queue();
         return false;
      }
   }
}