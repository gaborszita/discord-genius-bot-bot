package geniusBot.musicUtilities.youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import org.json.*;

import geniusBot.Keys;

public class Youtube
{  
   public static String searchForYoutubeVideo(String request)
           throws IOException, YoutubeAPIException
   {
      request = URLEncoder.encode(request, StandardCharsets.UTF_8.toString());
      URL url = new URL("https://www.googleapis.com/youtube/v3/search?q=" +
            request + "&maxResults=1&type=video&key=" +
              Keys.YOUTUBE_API_KEY);

      StringBuilder sb = new StringBuilder(1000);

      String line;

      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      BufferedReader br;
      int responseCode = connection.getResponseCode();
      if(responseCode == 200)
      {
         br = new BufferedReader(new InputStreamReader(connection.
                 getInputStream(), StandardCharsets.UTF_8));
      }
      else
      {
         throw new YoutubeAPIException(responseCode);
      }

      while ((line = br.readLine()) != null) 
      {
         sb.append(line);
      }
      JSONObject jsonObject = new JSONObject(sb.toString());

      JSONArray items = (JSONArray) jsonObject.get("items");
      Iterator<?> iterator = items.iterator();

      if (iterator.hasNext()) 
      {
         JSONObject id = (JSONObject)((JSONObject)iterator.next()).get("id");
         String videoId = (String)id.get("videoId");
         //JSONObject snippet = (JSONObject)((JSONObject)iterator.next()).get("snippet");
         //String title = (String)id.get("title");
         //System.out.println(title);
         //System.out.println(items);
         return "https://www.youtube.com/watch?v=" + videoId;
      }
      else
      {
         return null;
      }
   }
}
