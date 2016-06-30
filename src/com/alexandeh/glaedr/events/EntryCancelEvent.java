package com.alexandeh.glaedr.events;

import com.alexandeh.glaedr.scoreboards.Entry;
import com.alexandeh.glaedr.scoreboards.PlayerScoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when an entry is abruptly cancelled by entry#cancel
 * 
 * Copyright © 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted 
 * only if given explicit permission from original author: Alexander Maxwell
 */

public class EntryCancelEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	private Entry entry;
	private PlayerScoreboard scoreboard;
	private Player player;

	public EntryCancelEvent(Entry entry, PlayerScoreboard scoreboard)
	{
		this.entry = entry;
		this.scoreboard = scoreboard;
		this.player = scoreboard.getPlayer();
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
