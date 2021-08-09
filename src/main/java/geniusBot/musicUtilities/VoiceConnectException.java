package geniusBot.musicUtilities;

class VoiceConnectException extends Exception
{
   String message;

   public VoiceConnectException()
   {
      message = "";
   }
   public VoiceConnectException(String s)
   {
      message = s;
   }

   @Override
   public String getMessage()
   {
      return message;
   }
}
