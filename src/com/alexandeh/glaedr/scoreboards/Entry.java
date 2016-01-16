package com.alexandeh.glaedr.scoreboards;

import com.alexandeh.glaedr.Glaedr;
import com.alexandeh.glaedr.events.EntryCancelEvent;
import com.alexandeh.glaedr.events.EntryTickEvent;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Getter
public class Entry {

    private PlayerScoreboard playerScoreboard;
    private boolean countdown;
    private BigDecimal time;
    private Team team;
    private String id;
    private String uniqueString;
    private String text;

    /**
     * @param id               Unique string to identify and pull an entry after it is created
     * @param playerScoreboard A player's scoreboard, which you can get by calling PlayerScoreboard#getScoreboard
     */
    public Entry(String id, PlayerScoreboard playerScoreboard) {
        this.id = id;
        this.playerScoreboard = playerScoreboard;
    }

    /**
     * @param text Will be used to display on scoreboard and inserted before time if applicable
     * @return Entry
     */
    public Entry setText(String text) {
        Validate.notNull(text, "Text cannot be null!");
        this.text = text;
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
     * Removes Entry attachment to playerScoreboard
     */
    private void stop() {
        playerScoreboard.getScoreboard().resetScores(uniqueString);
        playerScoreboard.getScores().remove(this);
        playerScoreboard.getEntries().remove(this);
        playerScoreboard.getPlayer().setScoreboard(playerScoreboard.getScoreboard());
    }

    /**
     * Sets up team information, and sends Entry to PlayerScoreboard, should be called after all other values are set
     *
     * @return Entry
     */
    public Entry send() {
        Scoreboard scoreboard = playerScoreboard.getScoreboard();
        uniqueString = playerScoreboard.getNewUniqueString(this);
        if (scoreboard.getTeam(uniqueString) != null) {
            team = scoreboard.getTeam(uniqueString);
        } else {
            team = scoreboard.registerNewTeam(uniqueString);
        }

        Player player = playerScoreboard.getPlayer();
        playerScoreboard.getEntries().add(this);
        if (!(countdown)) {
            sendToScoreboard(text);
            player.setScoreboard(playerScoreboard.getScoreboard());
            return this;
        }

        new BukkitRunnable() {
            int minCount = 0;
            public void run() {
                if (time.doubleValue() <= 60) {
                    time = time.subtract(BigDecimal.valueOf(0.1));
                    String newText = text + " " + time + "s";
                    Bukkit.getPluginManager().callEvent(new EntryTickEvent(Entry.this, playerScoreboard));
                    if (time.doubleValue() <= 0) {
                        stop();
                        Bukkit.getPluginManager().callEvent(new EntryCancelEvent(Entry.this, playerScoreboard));
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
                    int minutes = time.intValue() / 60;
                    int seconds = time.intValue() % 60;
                    DecimalFormat formatter = new DecimalFormat("00");
                    String newText = text + " " + formatter.format(hours) + ":" + formatter.format(minutes) + ":" + formatter.format(seconds) + "h";

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