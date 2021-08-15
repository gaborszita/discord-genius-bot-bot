# Discord Genius Bot - bot code 

Main repository of this project: [https://github.com/gaborszita/discord-genius-bot](https://github.com/gaborszita/discord-genius-bot)

Bot setup:

1. Database setup:

   Create a new database and import the db.sql file. You should also create a 
   new user for your bot and allow it to access the database.

2. API Keys and secrets:

   The bot's config is stored in the ```botconfig.txt``` file and is read 
   every time the bot is started.

   An example of this config file is supplied in the 
   ```botconfig.example.txt``` file. Copy this file with the name 
   ```botconfig.txt``` and place it in the working 
   directory, which, in this case would be the ```target``` directory. Next, 
   edit the file and replace all ```YOUR_SOMETHING``` entries with the correct 
   value.

3. Compiling and creating the executable jar file:
   
   First, make sure maven is installed on your system. Create the executable 
   jar file by running ```mvn package```. The jar file should be in the target 
   directory. This contains all resources needed to run the bot.

4. Starting the bot:
   
   Start the bot by running 
   ```java -jar "DiscordGeniusBot-0.0-SNAPSHOT.jar"``` in the target directory.

   You can run this file anywhere, it has everything included. So, for 
   example, if you want to run this bot on a server, you just have to copy 
   this jar file. Make sure that you also copy the ```botconfig.txt``` file 
   and place it in the java working directory. (By default it's the same as 
   where the jar file is located.)