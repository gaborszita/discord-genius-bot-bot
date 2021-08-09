package geniusBot.musicUtilities.musicPlayer;

import java.io.IOException;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.tools.*;
import com.sedmelluq.discord.lavaplayer.track.*;

import geniusBot.musicUtilities.youtube.Youtube;
import geniusBot.musicUtilities.youtube.YoutubeAPIException;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;


public class MyAudioLoadResultHandler implements AudioLoadResultHandler
{
   TextChannel channel;
   TrackScheduler trackScheduler;
   AudioPlayerManager playerManager;
   String request;
   
   public MyAudioLoadResultHandler(TextChannel channel,
                                   TrackScheduler trackScheduler,
                                   AudioPlayerManager playerManager,
                                   String request)
   {
      this.channel = channel;
      this.trackScheduler = trackScheduler;
      this.playerManager = playerManager;
      this.request = request;
   }
   
   private MyAudioLoadResultHandler(TextChannel channel,
         TrackScheduler trackScheduler)
   {
      this.channel = channel;
      this.trackScheduler = trackScheduler;
   }
   
   @Override
   public void trackLoaded(AudioTrack track) 
   {
      TrackSchedulerResult status = trackScheduler.queue(track);
      if(status==TrackSchedulerResult.TRACK_STARTED)
      {
         channel.sendMessage(":play_pause: Playing track.").queue();
      }
      else if(status==TrackSchedulerResult.TRACK_QUEUED)
      {
         channel.sendMessage(":musical_note: Track has been added to"
               + " queue.").queue();
      }
      else if(status==TrackSchedulerResult.QUEUE_FULL)
      {
         channel.sendMessage(":exclamation:  The queue has reached i"
               + "ts maximum capacity (" + TrackScheduler.
               MAX_QUEUE_LENGTH + "), failed to add track to "
               + "queue :exclamation:").queue();
      }
   }

   @Override
   public void playlistLoaded(AudioPlaylist playlist) 
   {
      for (AudioTrack track : playlist.getTracks())
         trackScheduler.queue(track);

      channel.sendMessage(":play_pause: Playing playlist.").
         queue();
   }

   @Override
   public void noMatches()
   {
      try
      {
         request = Youtube.searchForYoutubeVideo(request);
      }
      catch(YoutubeAPIException e)
      {
         channel.sendMessage(":warning: Our Youtube API is unavailable " +
                 "at this time. Please try again in a few minutes.").queue();
         return;
      }
      catch (IOException e)
      {
         e.printStackTrace();
         return;
      }
      if(request!=null)
      {
         playerManager.loadItem(request, new MyAudioLoadResultHandler(
                         channel, trackScheduler)
         {
            @Override
            public void noMatches()
            {
               // Notify the user that we've got nothing
               channel.sendMessage(":information_source: No match found.").queue();
            }
         }
               );
      }
      else
      {
         channel.sendMessage(":information_source: No match found.").queue();
      }
   }

   @Override
   public void loadFailed(FriendlyException throwable) 
   {
      //throwable.printStackTrace();
      // Notify the user that everything exploded
      channel.sendMessage("Load failed.")
              .queue();
   }
}
