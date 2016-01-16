package com.alexandeh.glaedr.scoreboards;

import com.alexandeh.glaedr.Glaedr;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

@Getter
public class PlayerScoreboard {

    private HashSet<Entry> entries = new HashSet<>();
    private Scoreboard scoreboard;
    private Objective objective;
    private Player player;
    private static HashSet<PlayerScoreboard> scoreboards = new HashSet<>();
    private Map<Entry, Integer> scores = new HashMap<>();
    private Map<Entry, String> entryNames = new HashMap<>();
    private Glaedr glaedr = Glaedr.getInstance();
    private Map<Entry, Integer> bottomPlaceHolders = new HashMap<>();
    private List<Entry> topPlaceHolders = new ArrayList<>();

    /**
     * PlayerScoreboard constructor
     * No need to instantiate this class, it is all done within Glaedr
     *
     * @param player
     */
    public PlayerScoreboard(Player player) {
        Validate.notNull(player, "Player cannot be null!");
        this.player = player;
        this.attemptHook();


        getScoreboards().add(this);
    }

    /**
     * This method returns a unique String that will be used to prevent scoreboard flickering
     * It will return null if all ChatColors are currently being used
     *
     * @param entry Scoreboard Entry
     * @return String
     */
    public String getNewUniqueString(Entry entry) {
        for (ChatColor color : ChatColor.values()) {
            String text;
            if (entry.getText().length() >= 16) {
                text = entry.getText().substring(0, 16);
            } else {
                text = entry.getText();
            }
            if (!(entryNames.values().contains(color + "" + ChatColor.WHITE + ChatColor.getLastColors(text)))) {
                entryNames.put(entry, color + "" + ChatColor.WHITE + ChatColor.getLastColors(text));
                return color + "" + ChatColor.WHITE + ChatColor.getLastColors(text);
            }
        }
        return null;
    }

    /**
     * This method will check whether a boolean in the main class is true, if so it will
     * attempt to "hook" into a players already existing scoreboard.
     * If either the player has no existing scoreboards, or the boolean is false, it will
     * create a new scoreboard along with a new objective.
     */
    private void attemptHook() {
        if (glaedr.isHook()) {
            if (player.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
                this.scoreboard = player.getScoreboard();
                if (this.scoreboard.getObjective(DisplaySlot.SIDEBAR) != null) {
                    this.objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
                } else {
                    this.objective = scoreboard.registerNewObjective(player.getName(), "dummy");
                    this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    this.objective.setDisplayName(glaedr.getTitle());
                }
            } else {
                this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                this.objective = scoreboard.registerNewObjective(player.getName(), "dummy");
                this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                this.objective.setDisplayName(glaedr.getTitle());
            }
        } else {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.objective = scoreboard.registerNewObjective(player.getName(), "dummy");
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            this.objective.setDisplayName(glaedr.getTitle());
        }
    }



    /**
     * This will return a HashSet that contains all existing PlayerScoreboards
     *
     * @return PlayerScoreboard HashSet
     */
    public static HashSet<PlayerScoreboard> getScoreboards() {
        return scoreboards;
    }

    /**
     * @param player Player paramater, required to compare names
     * @return PlayerScoreboard
     */
    public static PlayerScoreboard getScoreboard(Player player) {
        Validate.notNull(player, "Player cannot be null!");
        for (PlayerScoreboard playerScoreboard : scoreboards) {
            if (playerScoreboard.getPlayer().getName().equalsIgnoreCase(player.getName())) {
                return playerScoreboard;
            }
        }
        return null;
    }

    /**
     * @param entry Used to determine if entry is already contained in map, etc
     * @return Integer used to set an entry's position on scoreboard
     */
    public int getScore(Entry entry) {
        for (int scoreint = 0; scoreint < entries.size() + 1; scoreint++) {
            if (scores.containsKey(entry)) {
                if (scores.get(entry) == scoreint + 1) {
                    if (!scores.values().contains(scoreint) && scoreint != 0) {
                        scores.put(entry, scoreint);
                        return scoreint;
                    }

                    return scoreint + 1;
                }
            } else {
                if (!scores.isEmpty()) {
                    if (!scores.values().contains(scoreint + 1)) {
                        scores.put(entry, scoreint + 1);
                        return scoreint + 1;
                    }
                } else {
                    scores.put(entry, scoreint + 1);
                    return scoreint + 1;
                }
            }
        }

        return 0;
    }
}
