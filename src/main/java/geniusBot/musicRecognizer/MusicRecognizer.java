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

import geniusBot.Keys;
import geniusBot.Paths;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class MusicRecognizer extends ListenerAdapter implements Runnable
{
   private volatile boolean active = true;
   private volatile int milliSecondsTimeout;
   private volatile boolean recordEnd = false;
   private volatile boolean record = false;
   private volatile boolean recordTerminate = false;
   private volatile boolean forcedRecordTerminate = false;
   private volatile boolean recording = false;
   private final AudioManager audioManager;
   private final MusicRecognizerManager recognizerManager;
   private volatile Member member;
   private final MusicRecognizerResultHandler resultHandler;
   private final JDA jda;
   private static final String TEMP_AUDIO_FOLDER = "audiotemp";

   MusicRecognizer(@NotNull AudioManager audioManager,
                   @NotNull MusicRecognizerResultHandler resultHandler,
                   @NotNull JDA jda, @NotNull MusicRecognizerManager
                           recognizerManager)
   {
      this.audioManager = audioManager;
      this.resultHandler = resultHandler;
      this.jda = jda;
      this.recognizerManager = recognizerManager;
   }

   public void destroy()
   {
      active = false;
      jda.removeEventListener(this);
      for(MusicRecognizerHolder holder : recognizerManager.musicRecognizers)
      {
         if(holder.musicRecognizer == this)
         {
            try
            {
               holder.thread.join();
            }
            catch (InterruptedException e)
            {
               e.printStackTrace();
            }
            break;
         }
      }
   }

   public boolean record(Member member, int secondsTimeout, boolean force)
   {
      if(force || !recording)
      {
         forcedRecordTerminate = true;
         if(record)
            recordTerminate = true;
         this.member = member;
         this.milliSecondsTimeout = secondsTimeout * 1000;
         record = true;
         return true;
      }
      else
      {
         return false;
      }
   }

   @Override
   public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event)
   {
      if(event.getMember().equals(member))
      {
         recordTerminate = true;
      }
   }

   @Override
   public void onGuildVoiceMute(@NotNull GuildVoiceMuteEvent event)
   {
      if(event.isMuted() && event.getMember().equals(member))
      {
         recordEnd = true;
      }
   }

   private File writeToWavFile(@NotNull byte[] decodedData) throws IOException
   {
      File outFile = File.createTempFile("tmp", ".wav",
              new File(Paths.webDataLocation + "/" +
                      TEMP_AUDIO_FOLDER));
      AudioSystem.write(new AudioInputStream(new ByteArrayInputStream(
              decodedData), AudioReceiveHandler.OUTPUT_FORMAT,
                      decodedData.length), AudioFileFormat.Type.WAVE, outFile);
      return outFile;
   }

   @Override
   public void run()
   {
      while (active)
      {
         if (record)
         {
            recording = true;
            record = false;
            recordEnd = false;
            File audioRecordFile;
            UserAudioRecorder userAudioRecorder = new
                    UserAudioRecorder(member.getUser(), milliSecondsTimeout
                    /20);
            long millisSecondsStart = System.currentTimeMillis();
            audioManager.setReceivingHandler(userAudioRecorder);
            while (!recordEnd && active && System.currentTimeMillis()
                    -millisSecondsStart<milliSecondsTimeout &&
                    !recordTerminate)
            {
               try
               {
                  Thread.sleep(100);
               }
               catch (InterruptedException e)
               {
                  e.printStackTrace();
               }
            }
            audioManager.setReceivingHandler(null);
            if (active && !recordTerminate)
            {
               resultHandler.recognizeInProgress();
               // decode audio data
               try
               {
                  int size = 0;
                  for (byte[] bs : userAudioRecorder.recordedAudio)
                  {
                     size += bs.length;
                  }
                  byte[] decodedData = new byte[size];
                  int i = 0;
                  for (byte[] bs : userAudioRecorder.recordedAudio)
                  {
                     for (byte b : bs)
                     {
                        decodedData[i++] = b;
                     }
                  }
                  // write audio data to wav file on web server
                  audioRecordFile = writeToWavFile(decodedData);

                  System.setProperty("http.agent", "Chrome");
                  URL url = new URL("https://api.audd.io/" +
                          "recognizeWithOffset/?url=" +
                          Paths.dashboardURI + "/" + TEMP_AUDIO_FOLDER +
                          "/" + audioRecordFile.getName() + "&api_token=" +
                          Keys.AUDD_API_KEY);

                  StringBuilder sb = new StringBuilder(350);

                  String line;

                  HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                  BufferedReader br;
                  int responseCode = connection.getResponseCode();
                  //connection.connect();
                  if(!audioRecordFile.delete())
                     System.out.println("[" + Thread.currentThread().getName()
                             + "] " + "Warning: Failed to delete temporary " +
                             "file.");
                  if (responseCode == 200)
                  {
                     br = new BufferedReader(new InputStreamReader(connection.
                             getInputStream(), StandardCharsets.UTF_8));

                     while ((line = br.readLine()) != null)
                     {
                        sb.append(line);
                     }

                     JSONObject jsonObject = new JSONObject(sb.toString());
                     String status = (String) jsonObject.get("status");
                     if(status.equals("success"))
                     {

                        if (jsonObject.has("result") &&
                                !jsonObject.isNull("result"))
                        {
                           JSONObject result =
                                   (JSONObject) jsonObject.get("result");

                           if(result.has("list") &&
                                   !result.isNull("list"))
                           {
                              JSONArray items =
                                      (JSONArray) result.get("list");
                              Iterator<?> iterator = items.iterator();

                              if(iterator.hasNext())
                              {
                                 JSONObject songData =
                                         ((JSONObject)iterator.next());
                                 String artist = (String)songData.get("artist");
                                 String title = (String)songData.get("title");

                                 new Thread(() -> resultHandler.musicRecognized
                                         (artist, title)).start();
                              }
                           }
                        }
                        else
                        {
                           new Thread(
                                   resultHandler::musicRecognizeFailed).start();
                        }
                     }
                     else
                     {
                        new Thread(resultHandler::musicRecognizeFailed).start();
                     }
                  }
                  else
                  {
                     new Thread(resultHandler::musicRecognizeError).start();
                  }
               }
               catch (IOException e)
               {
                  e.printStackTrace();
               }
            }
            else
            {
               recordTerminate = false;
               if(!forcedRecordTerminate)
                  new Thread(resultHandler::recognizeTerminated).start();
               else
                  forcedRecordTerminate = false;
            }
         }
         recording = false;
         try
         {
            Thread.sleep(100);
         }
         catch (InterruptedException e)
         {
            e.printStackTrace();
         }
      }
   }
}
