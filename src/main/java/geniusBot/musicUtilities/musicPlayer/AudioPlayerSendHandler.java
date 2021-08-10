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

package geniusBot.musicUtilities.musicPlayer;

import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {
   private final AudioPlayer audioPlayer;
   private AudioFrame lastFrame;

   public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
     this.audioPlayer = audioPlayer;
   }

   @Override
   public boolean canProvide() {
     lastFrame = audioPlayer.provide();
     return lastFrame != null;
   }

   @Override
   public ByteBuffer provide20MsAudio() {
     return ByteBuffer.wrap(lastFrame.getData());
   }

   @Override
   public boolean isOpus() {
     return true;
   }
 }
