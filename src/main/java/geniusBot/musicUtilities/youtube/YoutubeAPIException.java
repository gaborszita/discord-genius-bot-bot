package geniusBot.musicUtilities.youtube;

public class YoutubeAPIException extends Exception
{
   private int responseCode = 0;


   YoutubeAPIException(int code)
   {
      this.responseCode = code;
   }

   @Override
   public String getMessage()
   {
      String returnmessage =  "A Youtube API request encountered an error.";
      if(responseCode != 0)
         returnmessage += "\nThe Youtube API responded with code " + responseCode ;
      return returnmessage;
   }
}
