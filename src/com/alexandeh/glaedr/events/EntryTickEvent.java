package com.alexandeh.glaedr.events;

import com.alexandeh.glaedr.scoreboards.Entry;
import com.alexandeh.glaedr.scoreboards.PlayerScoreboard;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@Setter
/**
 * This event is called when an entry is ticked
 */
public class EntryTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Entry entry;
    private PlayerScoreboard scoreboard;
    private Player player;

    public EntryTickEvent(Entry entry, PlayerScoreboard scoreboard) {
        this.entry = entry;
        this.scoreboard = scoreboard;
        this.player = scoreboard.getPlayer();
    }
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
