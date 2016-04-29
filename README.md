# Glaedr
Extensive yet simplistic Scoreboard API for the Bukkit/Spigot API

## Beta Version Changelog
+ Removed how each entry creates it's own BukkitTask, each entry is looped through and edited in one central task. Instead of one task per Entry, there is now one task per PlayerScoreboard. This is extremely more efficient allowing all entries to countdown in sync.
+ Wrappers/Spacers have been added. This will allow you to add a line above and/or below all entries. ![alt tag](https://i.gyazo.com/c72ca55810ae730e0bd4345170707f17.png)
+ Scoreboard "scores" now counts down from 15 to 1, instead of from 1 to 15. This will soon be optional.
+ The new version is some-what backwards compatible. entry#cancel and entry#pause have been replaced with entry#setCancelled(boolean) and entry#setPaused(boolean).
+ Entries that do not countup nor countdown will still update, so that you can edit the text later and it will update on the scoreboard. This also will keep the position of the entry updated.
+ Installation has been changed a bit.

##### As with any other beta, expect bugs. Feel free to fix them yourself and submit a pull request, or simply file an issue and it will be looked at ASAP.

## Installation
1. Pull the latest version of Glaedr or download it here.(https://www.dropbox.com/s/52cgyy53unvf2sn/Glaedr.jar?dl=1)
2. Add it to your project's build path. (Note: Make sure to extract Glaedr.jar into your output path, so people don't need to add Glaedr into their server!)
3. Instantiate Glaedr, you should have something like this in your main class:

  ```java
  private Glaedr glaedr;
  
  public void onEnable() {
    glaedr = new Glaedr(this, title, hook, overrideTitle, scoreCountUp);
    glaedr.getBottomWrappers().add("&7&m--------------------");
    glaedr.getTopWrappers().add("&7&m--------------------");
    glaedr.registerPlayers();
  }
  
  ```
  
4. Done! Glaedr is now completely set up!

## Usage
Here is some example usage, the following code will create a scoreboard entry with a countdown of 16 seconds:

  ```java
 PlayerScoreboard scoreboard = PlayerScoreboard.getScoreboard(player);
 new Entry("enderpearl", scoreboard)
 .setCountdown(true)
 .setText(ChatColor.GOLD + "Enderpearl Cooldown" + ChatColor.GRAY + ":")
 .setTime(16)
 .send();
  ```
#### Result:
![alt tag](https://i.gyazo.com/586dfb3c8a842cc0b0a0c974baf699f3.gif)
