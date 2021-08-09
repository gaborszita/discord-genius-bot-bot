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
