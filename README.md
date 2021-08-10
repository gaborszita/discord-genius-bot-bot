# Discord Genius Bot - bot code 

Main repository of this project: [https://github.com/gaborszita/discord-genius-bot](https://github.com/gaborszita/discord-genius-bot)

Bot setup:

1. Database setup:

   Create a new database and import the db.sql file. You should also create a 
   new user for your bot and allow it to access the database.

2. API Keys and secrets:

   In the Keys.java file, enter your Discord token where it says 
   ```YOUR_DISCORD_TOKEN``` and your YouTube API key where it says 
   ```YOUR_YOUTUBE_API_KEY```.

   Open the MySQLConnection.java file (located in the geniusBot.mySQLutils 
   package) and replace ```dbusername``` with the username your bot can 
   connect to the database, ```dbpassword``` with the database password, and
   ```dbname``` with the name of the database.

3. Compiling and creating the executable jar file:
   
   First, make sure maven is installed on your system. Create the executable 
   jar file by running ```mvn package```. The jar file should be in the target 
   directory. This contains all resources needed to run the bot.

4. Start the bot by running 
   ```java -jar "DiscordGeniusBot-0.0-SNAPSHOT.jar"``` in the target directory.

   You run this file anywhere, it has everything included. So, for 
   example, if you want to run this bot on a server, just have to copy this 
   jar file.