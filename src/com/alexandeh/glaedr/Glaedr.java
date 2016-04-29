package com.alexandeh.glaedr;

import com.alexandeh.glaedr.scoreboards.Entry;
import com.alexandeh.glaedr.scoreboards.PlayerScoreboard;
import com.alexandeh.glaedr.scoreboards.Wrapper;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
@Getter
public class Glaedr implements Listener {

    private static JavaPlugin plugin; //TODO: Clean this up a bit
    private String title;
    private boolean hook, overrideTitle, scoreCountUp;
    private List<String> bottomWrappers, topWrappers;

    public Glaedr(JavaPlugin plugin, String title, boolean hook, boolean overrideTitle, boolean scoreCountUp) {
        Glaedr.plugin = plugin;
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.hook = hook;
        this.overrideTitle = overrideTitle;
        this.scoreCountUp = scoreCountUp;

        bottomWrappers = new ArrayList<>();
        topWrappers = new ArrayList<>();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Glaedr(JavaPlugin plugin, String title) {
        this(plugin, title, false, true, false);
    }

    public   void registerPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            long oldTime = System.currentTimeMillis();
            new PlayerScoreboard(this, player);
            player.sendMessage(ChatColor.BLUE + "Scoreboard created in " + (System.currentTimeMillis() - oldTime) + "ms.");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerScoreboard playerScoreboard = PlayerScoreboard.getScoreboard(player);
        if (playerScoreboard == null) {
            long oldTime = System.currentTimeMillis();
            new PlayerScoreboard(this, player);
            player.sendMessage(ChatColor.BLUE + "Scoreboard created in " + (System.currentTimeMillis() - oldTime) + "ms.");
        } else {

            if (player.getScoreboard() != playerScoreboard.getScoreboard()) {

                if (player.getScoreboard().getObjective(DisplaySlot.SIDEBAR) != null) {
                    playerScoreboard.setObjective(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR));
                } else {
                    Objective objective = player.getScoreboard().registerNewObjective(player.getName(), "dummy");
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    objective.setDisplayName(title);
                    playerScoreboard.setObjective(objective);
                }

                playerScoreboard.setScoreboard(player.getScoreboard());
                for (Entry entry : playerScoreboard.getEntries()) {
                    entry.setup();
                }
                for (Wrapper wrapper : playerScoreboard.getWrappers()) {
                    wrapper.setup();
                }
            }

            player.setScoreboard(playerScoreboard.getScoreboard());
        }
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }
}