package com.alexandeh.glaedr.scoreboards;

import com.alexandeh.glaedr.Glaedr;
import com.alexandeh.glaedr.events.EntryCancelEvent;
import com.alexandeh.glaedr.events.EntryFinishEvent;
import com.alexandeh.glaedr.events.EntryPauseEvent;
import com.alexandeh.glaedr.events.EntryTickEvent;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Getter
public class Entry {

    private PlayerScoreboard playerScoreboard;
    private boolean countdown, countup;
    private BigDecimal time;
    private Team team;
    private String id;
    private String text, textTime;
    private String uniqueString = null;
    private BukkitTask task = null;
    private boolean paused;

    /**
     * @param id Unique string to identify and pull an entry after it is created
     * @param playerScoreboard A player's scoreboard, which you can get by calling PlayerScoreboard#getScoreboard
     */
    public Entry(String id, PlayerScoreboard playerScoreboard) {
        this.id = id;
        this.time = BigDecimal.ZERO;
        this.playerScoreboard = playerScoreboard;
        for (Entry entry : playerScoreboard.getEntries()) {
            if (entry.getId().equalsIgnoreCase(id) && entry != this) {
                entry.cancel();
            }
        }
    }

    /**
     * @param text Will be used to display on scoreboard and inserted before time if applicable
     * @return Entry
     */
    public Entry setText(String text) {
        Validate.notNull(text, "Text cannot be null!");
        this.text = ChatColor.translateAlternateColorCodes('&', text);
        return this;
    }

    /**
     * @param countup Boolean that will set countup to
     * @return Entry
     */
    public Entry setCountup(boolean countup) {
        this.countup = countup;
        return this;
    }

    /**
     * @param countdown Whether entry will be static or countdown, time cannot be null
     * @return Entry
     */
    public Entry setCountdown(boolean countdown) {
        this.countdown = countdown;
        return this;
    }

    /**
     * @param time Countdown time, countdown must be true
     * @return Entry
     */
    public Entry setTime(BigDecimal time) {
        this.time = time;
        return this;
    }

    /**
     * @param time Countdown time, countdown must be true
     * @return Entry
     */
    public Entry setTime(double time) {
        this.time = BigDecimal.valueOf(time);
        return this;
    }

    /**
     * Sends update to scoreboard, does String setup and score settings
     */

    private void sendToScoreboard(String input) {
        Objective objective = playerScoreboard.getObjective();
        if (!(team.getEntries().contains(uniqueString))) {
            team.addEntry(uniqueString);
        }
        String prefix = "", suffix = "";
        if (input.length() > 32) {
            throw new StringIndexOutOfBoundsException("Entry text must be under 32 characters!");
        }

        if (input.length() <= 16) {
            prefix = input;
        } else if (input.length() > 16) {
            prefix = input.substring(0, 16);
            suffix = input.substring(16, input.length());
        }

        team.setPrefix(prefix);
        team.setSuffix(suffix);
        Score score = objective.getScore(uniqueString);
        if (!score.isScoreSet()) {
            score.setScore(playerScoreboard.getScore(this));
        } else {
            if (score.getScore() > playerScoreboard.getScore(this)) {
                playerScoreboard.getScoreboard().resetScores(uniqueString);
                score.setScore(playerScoreboard.getScore(this));
            }
        }
    }

    /**
     * Abruptly cancels the entry, calling a new EntryCancelEvent
     */
    public void cancel() {
        stop();
        Bukkit.getPluginManager().callEvent(new EntryCancelEvent(this, playerScoreboard));
    }

    /**
     * Pauses entry countdown if it's running
     */
    public void pause() {
        if (task != null) {
            task.cancel();
            paused = true;
            Bukkit.getPluginManager().callEvent(new EntryPauseEvent(Entry.this, playerScoreboard));
        }
    }

    /**
     * Removes Entry attachment to playerScoreboard
     */
    private void stop() {
        playerScoreboard.getScoreboard().resetScores(uniqueString);
        playerScoreboard.getScores().remove(this);
        playerScoreboard.getEntries().remove(this);
        playerScoreboard.getPlayer().setScoreboard(playerScoreboard.getScoreboard());
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * Sets up team information, and sends Entry to PlayerScoreboard, should be called after all other values are set
     *
     * @return Entry
     */
    public Entry send() {
        Scoreboard scoreboard = playerScoreboard.getScoreboard();
        if (uniqueString == null) {
            uniqueString = playerScoreboard.getNewUniqueString(this);
        }
        if (scoreboard.getTeam(uniqueString) != null) {
            team = scoreboard.getTeam(uniqueString);
        } else {
            team = scoreboard.registerNewTeam(uniqueString);
        }

        Player player = playerScoreboard.getPlayer();
        playerScoreboard.getEntries().add(this);
        if (!(countdown)) {
            if (countup) {
                paused = false;

                task = new BukkitRunnable() {
                    int minCount = 0;
                    public void run() {
                        if (time.doubleValue() <= 60) {
                            time = time.add(BigDecimal.valueOf(0.1));
                            String newText = text + " " + time + "s";
                            textTime = time + "s";
                            Bukkit.getPluginManager().callEvent(new EntryTickEvent(Entry.this, playerScoreboard));

                            sendToScoreboard(newText);
                            player.setScoreboard(scoreboard);
                        } else if (time.doubleValue() <= 3600) {
                            int minutes = time.intValue() / 60;
                            int seconds = time.intValue() % 60;
                            DecimalFormat formatter = new DecimalFormat("00");
                            String newText = text + " " +  formatter.format(minutes) + ":" + formatter.format(seconds) + "m";
                            textTime = formatter.format(minutes) + ":" + formatter.format(seconds) + "m";

                            minCount++;

                            if (minCount == 10) {
                                time = time.add(BigDecimal.valueOf(1));

                                Bukkit.getPluginManager().callEvent(new EntryTickEvent(Entry.this, playerScoreboard));
                                sendToScoreboard(newText);
                                player.setScoreboard(scoreboard);
                                minCount = 0;
                            }
                        } else {
                            int hours = time.intValue() / 3600;
                            int minutes = (time.intValue() % 3600) / 60;
                            int seconds = time.intValue() % 60;
                            DecimalFormat formatter = new DecimalFormat("00");
                            String newText = text + " " + formatter.format(hours) + ":" + formatter.format(minutes) + ":" + formatter.format(seconds) + "h";
                            textTime = formatter.format(hours) + ":" + formatter.format(minutes) + ":" + formatter.format(seconds) + "h";
                            minCount++;

                            if (minCount == 10) {
                                time = time.add(BigDecimal.valueOf(1));

                                Bukkit.getPluginManager().callEvent(new EntryTickEvent(Entry.this, playerScoreboard));
                                sendToScoreboard(newText);
                                player.setScoreboard(scoreboard);
                                minCount = 0;
                            }
                        }
                    }
                }.runTaskTimer(Glaedr.getInstance().getPlugin(), 2L, 2L);
                return this;
            }
            sendToScoreboard(text);
            player.setScoreboard(playerScoreboard.getScoreboard());
            return this;
        }

        paused = false;

        task = new BukkitRunnable() {
            int minCount = 0;
            public void run() {
                if (time.doubleValue() <= 60) {
                    time = time.subtract(BigDecimal.valueOf(0.1));
                    String newText = text + " " + time + "s";
                    textTime = time + "s";
                    Bukkit.getPluginManager().callEvent(new EntryTickEvent(Entry.this, playerScoreboard));
                    if (time.doubleValue() <= 0) {
                        stop();
                        Bukkit.getPluginManager().callEvent(new EntryFinishEvent(Entry.this, playerScoreboard));
                        this.cancel();
                        return;
                    }

                    sendToScoreboard(newText);
                    player.setScoreboard(scoreboard);
                } else if (time.doubleValue() <= 3600) {
                    int minutes = time.intValue() / 60;
                    int seconds = time.intValue() % 60;
                    DecimalFormat formatter = new DecimalFormat("00");
                    String newText = text + " " +  formatter.format(minutes) + ":" + formatter.format(seconds) + "m";
                    textTime = formatter.format(minutes) + ":" + formatter.format(seconds) + "m";

                    minCount++;

                    if (minCount == 10) {
                        time = time.subtract(BigDecimal.valueOf(1));

                        Bukkit.getPluginManager().callEvent(new EntryTickEvent(Entry.this, playerScoreboard));
                        sendToScoreboard(newText);
                        player.setScoreboard(scoreboard);
                        minCount = 0;
                    }
                } else {
                    int hours = time.intValue() / 3600;
                    int minutes = (time.intValue() % 3600) / 60;
                    int seconds = time.intValue() % 60;
                    DecimalFormat formatter = new DecimalFormat("00");
                    String newText = text + " " + formatter.format(hours) + ":" + formatter.format(minutes) + ":" + formatter.format(seconds) + "h";
                    textTime = formatter.format(hours) + ":" + formatter.format(minutes) + ":" + formatter.format(seconds) + "h";
                    minCount++;

                    if (minCount == 10) {
                        time = time.subtract(BigDecimal.valueOf(1));

                        Bukkit.getPluginManager().callEvent(new EntryTickEvent(Entry.this, playerScoreboard));
                        sendToScoreboard(newText);
                        player.setScoreboard(scoreboard);
                        minCount = 0;
                    }
                }
            }
        }.runTaskTimer(Glaedr.getInstance().getPlugin(), 2L, 2L);

        return this;
    }

}