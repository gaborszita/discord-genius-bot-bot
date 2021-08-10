-- MySQL dump 10.19  Distrib 10.3.29-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: discord_genius_bot
-- ------------------------------------------------------
-- Server version	10.3.29-MariaDB-0+deb10u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `counter_guild_data`
--

DROP TABLE IF EXISTS `counter_guild_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `counter_guild_data` (
  `guildId` bigint(20) NOT NULL,
  `nextCount` bigint(20) DEFAULT NULL,
  `lastCountMessageId` bigint(20) DEFAULT NULL,
  `lastCountSenderId` bigint(20) DEFAULT NULL,
  `botMessageSent` tinyint(1) DEFAULT NULL,
  `counterChannelId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`guildId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `counter_guild_data`
--

LOCK TABLES `counter_guild_data` WRITE;
/*!40000 ALTER TABLE `counter_guild_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `counter_guild_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `help_commands`
--

DROP TABLE IF EXISTS `help_commands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `help_commands` (
  `command` text DEFAULT NULL,
  `helpDescription` text DEFAULT NULL,
  `helpValue` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `help_commands`
--

LOCK TABLES `help_commands` WRITE;
/*!40000 ALTER TABLE `help_commands` DISABLE KEYS */;
INSERT INTO `help_commands` VALUES ('music','Music player help','g!connect - connects Genius Bot to your voice channel\ng!disconnect - disconnects Genius Bot from your voice channel\ng!play track - to play a track, track can be a url or even just the name of the song\ng!stop - to stop playing\ng!pause - to pause the currently playing track\ng!resume - to resume the currently playing track\ng!next - to skip to next track in the queue\ng!recognize - to recognize a song. Hum, sing, or even put your mic to a speaker that is playing a song and Genius Bot will recognize the song playing!\ng!recognize play - Same as g!recognize, but Genius Bot will start playing the song after it\'s recognized.');
/*!40000 ALTER TABLE `help_commands` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-08-09 23:48:30
