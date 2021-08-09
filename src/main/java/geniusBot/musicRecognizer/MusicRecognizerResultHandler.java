package geniusBot.musicRecognizer;

public interface MusicRecognizerResultHandler
{
   void musicRecognized(String artist, String songTitle);
   void musicRecognizeFailed();
   void musicRecognizeError();
   void recognizeTerminated();
   void recognizeInProgress();
}
