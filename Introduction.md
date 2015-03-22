# Introduction to JOverseer #

JOverseer is a program that assists players of the Middle Earth PBM game (www.middleearthgames.com) view and process their turn results. It is written in Java and is designed to be cross-platform.

JOverseer is tailored to grudge games, as it has many tools that manipulate the information gathered from the team's turn files. It can, however, be used for regular games as well.

# Main Features #

The main features, in summary, are:

  * Reads both XML and PDF files, and combines the information therein to produce a comprehensive overview of the turn.
  * Reads both the player's turn files and those of other players in the player's team.
  * Displays a graphical map of the game, which is updated with information from the turn results.
  * Displays additional information in tables and lists.

The information the game manages is:

**From the players' XML files:**
  * Characters,
  * Armies,
  * Populations centers,
  * Nation economy data,
  * Nation messages

**From the players' PDF files:**
  * Nation relations,
  * Population center production,
  * Character order results
  * Results of LA, LAT, RC and RCT spells,
  * Challenges, Combats and Encounters,
  * Companies,
  * Double Agents

## Advanced Display ##
JOverseer automatically parses the information retrieved from the turn files, combines it with background information from the game, and delivers it to the user in a user friendly manner. Thus, the user has as much information as possible when viewing the map. For instance:
  * The relevant hex is extracted from nation messages and the messages are shown on the map, along with the rest of the information about the hex.
  * Similar to nation messages, the results of LA, LAT, RC and RCT spells are shown on the map.
  * Whenever an enemy character is reported in a hex, his starting skills are automatically shown to the player for reference.
  * Hexes for which the player (or the players team) does not have any information (either from their map or from intelligence actions such as recons) are clearly marked on the map.
  * Using the nation messages, a list of enemy agents is automatically compiled and maintained.
  * Using the LA and LAT spell results, a list of artifacts, their locations and their owner, is automatically compiled and maintained.

Here is a screenshot of JOverseer in action:

![http://joverseer.googlecode.com/svn/wiki/images/joverseer-sshot1.jpg](http://joverseer.googlecode.com/svn/wiki/images/joverseer-sshot1.jpg)

## Other Features ##
JOverseer offers several other advanced features to help the player.
  * Players can write the orders for their characters using a point and click interface with minimal typing.
  * Players can import order files produced with Automagic.
  * Some orders can be visualized on the map (e.g. character and army movement, product transfers).
  * There are tools to automatically display on the map the range of a character or an army (i.e. the hexes that can be reached).
  * With the Track Character tool the player can receive a report of all the available information for a given character (friendly or enemy) from all the available turns.
  * Lots of other cool stuff.

## Current Status ##
JOverseer first beta version was made available in May 2007. JOverseer was officially released on June 23, 2008.

Currently it supports 1650, 2950, BOFA, Untold War and FA games.

JOverseer is being developed by Marios Skounakis.