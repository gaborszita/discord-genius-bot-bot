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
