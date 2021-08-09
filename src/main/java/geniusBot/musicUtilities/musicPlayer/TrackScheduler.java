package geniusBot.musicUtilities.musicPlayer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import geniusBot.musicUtilities.GuildAudioDataHolder;
import net.dv8tion.jda.api.entities.TextChannel;

public class TrackScheduler extends AudioEventAdapter
{
   public static final int TRACK_STARTED = 1;
   public static final int TRACK_QUEUED = 2;
   public static final int QUEUE_FULL = 3;
   
   public static final int MAX_QUEUE_LENGTH = 100;
   
   AudioPlayer player;
   BlockingQueue<AudioTrack> queue;
   volatile GuildAudioDataHolder audioDataHolder;
   volatile TextChannel channel;
   
   public TrackScheduler(AudioPlayer player,
                         GuildAudioDataHolder audioDataHolder)
   {
      this.player = player;
      this.queue = new ArrayBlockingQueue<>(MAX_QUEUE_LENGTH);
      this.audioDataHolder = audioDataHolder;
   }
   
   @Override
   public void onPlayerPause(AudioPlayer player) 
   {
      // Player was paused
      audioDataHolder.getBoundChannel().sendMessage(":pause_button: Track paused.").queue();
      // slightDestroyIfNotActive(false);
   }

   @Override
   public void onPlayerResume(AudioPlayer player) 
   {
      // Player was resumed
      audioDataHolder.getBoundChannel().sendMessage(":play_pause: Track resumed.").queue();
      slightDestroyIfNotActive(true);
   }

   @Override
   public void onTrackStart(AudioPlayer player, AudioTrack track) 
   {
      // A track started playing
      slightDestroyIfNotActive(true);
   }

   @Override
   public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) 
   {
      if (endReason.mayStartNext && !queue.isEmpty()) {
         audioDataHolder.getBoundChannel().sendMessage(":fast_forward: Next track started.").queue();
         player.startTrack(queue.poll(), false);
      }
      else if(endReason != AudioTrackEndReason.REPLACED)
      {
         slightDestroyIfNotActive(false);
      }

      // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
      // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
      // endReason == STOPPED: The player was stopped.
      // endReason == REPLACED: Another track started playing while this had not finished
      // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
      //                       clone of this back to your queue
   }

   @Override
   public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) 
   {
      // An already playing track threw an exception (track end event will still be received separately)
      // exception.printStackTrace();
      if (!queue.isEmpty()) {
         player.startTrack(queue.poll(), false);
      }
      else
      {
         slightDestroyIfNotActive(false);
      }
   }

   @Override
   public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) 
   {
      // Audio track has been unable to provide us any audio, might want to just start a new track
      if (!queue.isEmpty()) {
         player.startTrack(queue.poll(), false);
      }
      else
      {
         slightDestroyIfNotActive(false);
      }
   }
   
   public TrackSchedulerResult queue(AudioTrack track)
   {
      if(!player.startTrack(track, true))
      {
         if(queue.size()<MAX_QUEUE_LENGTH)
         {
            queue.add(track);
            return TrackSchedulerResult.TRACK_QUEUED;
         }
         else
         {
            return TrackSchedulerResult.QUEUE_FULL;
         }
      }
      else
      {
         return TrackSchedulerResult.TRACK_STARTED;
      }
   }

   public boolean next()
   {
      if(!queue.isEmpty())
      {
         player.startTrack(queue.poll(), false);
         return true;
      }
      else
      {
         return false;
      }
   }
   
   private void slightDestroyIfNotActive(boolean isActive)
   {
      if(!isActive)
      {
         audioDataHolder.playerSlightDestroy();
      }
   }
}

enum TrackSchedulerResult
{
   TRACK_STARTED,
   TRACK_QUEUED,
   QUEUE_FULL
}