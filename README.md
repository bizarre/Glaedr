# Glaedr
Extensive yet simplistic Scoreboard API for the Bukkit/Spigot API

## Installation
1. Pull the latest version of Glaedr or download it here.
2. Add it to your project's build path. (Note: Make sure to extract Glaedr.jar into your output path, so people don't need to add Glaedr into their server!)
3. Instantiate Glaedr, you should have something like this in your main class:

  ```java
  private Glaedr glaedr;
  
  public void onEnable() {
    glaedr = new Glaedr(this, title, hook);
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
