package com.alexandeh.glaedr;

import com.alexandeh.glaedr.listeners.ScoreboardListeners;
import com.alexandeh.glaedr.scoreboards.PlayerScoreboard;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * Copyright (c) 2016, Alexander Maxwell. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - The name of Alexander Maxwell may not be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * Glaedr is a simple Scoreboard API that features:
 *
 *   - Up to 32 characters per line
 *   - Automatic countdowns with millis
 *   - Effortless usability and easy-to-understand methods
 *   - Much more coming soon
 *
 */
@Getter
@Setter
public class Glaedr implements Listener {

    private String title = "Title";
    private boolean hook;
    private static Glaedr instance;
    private JavaPlugin plugin;

    /**
     * @param plugin Plugin instance, used to register scoreboard listeners
     * @param title Title of scoreboard, used as objective display name
     * @param hook Boolean to check whether Glaedr should attempt to hook into an already existing scoreboard
     */
    public Glaedr(JavaPlugin plugin, String title, boolean hook) {
        Bukkit.getPluginManager().registerEvents(new ScoreboardListeners(), plugin);
        this.plugin = plugin;
        this.title = title;
        this.hook = hook;
        instance = this;

        checkPlayers();
    }

    /**
     * @param plugin Plugin instance, used to register scoreboard listeners
     */
    public Glaedr(JavaPlugin plugin) {
        this(plugin, "Title", false);
    }

    /**
     * @param plugin Plugin instance, used to register scoreboard listeners
     * @param title Title of scoreboard, used as objective display name
     */
    public Glaedr(JavaPlugin plugin, String title) {
        this(plugin, title, false);
    }

    /**
     * @param plugin Plugin instance, used to register scoreboard listeners
     * @param hook Boolean to check whether Glaedr should attempt to hook into an already existing scoreboard
     */
    public Glaedr(JavaPlugin plugin, boolean hook) {
        this(plugin, "Title", hook);
    }

    /**
     * Creates a scorebaord for all online players
     */
    private void checkPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerScoreboard playerScoreboard = PlayerScoreboard.getScoreboard(player);
            if (playerScoreboard == null) {
                long startTime = System.currentTimeMillis();
                new PlayerScoreboard(player);
                long endTime = System.currentTimeMillis();
                player.sendMessage(ChatColor.GREEN + "Scoreboard created in " + (endTime - startTime) + "ms.");
            }
        }
    }

    /**
     * @return Glaedr instance
     */
    public static Glaedr getInstance() {
        return instance;
    }
}
