package geniusBot.musicRecognizer;

class MusicRecognizerHolder
{
   MusicRecognizer musicRecognizer;
   Thread thread;

   MusicRecognizerHolder(MusicRecognizer musicRecognizer, Thread thread)
   {
      this.musicRecognizer = musicRecognizer;
      this.thread = thread;
   }
}
