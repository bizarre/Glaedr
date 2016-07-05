package com.alexandeh.glaedr.scoreboards;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Copyright © 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted 
 * only if given explicit permission from original author: Alexander Maxwell
 */

public class Entry
{
	private PlayerScoreboard playerScoreboard;
	private boolean countdown, countup;
	private String text, id, key, textTime, originalText;
	private BigDecimal time;
	private int interval;
	private Team team;
	private boolean cancelled, paused, set, bypassAutoFormat, removeTimeSuffix;

	public Entry(String id, PlayerScoreboard playerScoreboard)
	{
		this.id = id;
		this.playerScoreboard = playerScoreboard;
	}
	
	public boolean isCountdown()
	{
		return countdown;
	}
	
	public boolean isCountup()
	{
		return countup;
	}
	
	public String getText()
	{
		return text;
	}
	
	public String getId()
	{
		return id;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public BigDecimal getTime()
	{
		return time;
	}
	
	public int getInterval()
	{
		return interval;
	}
	
	public boolean isCancelled()
	{
		return cancelled;
	}
	
	public boolean isPaused()
	{
		return paused;
	}
	
	public boolean isBypassAutoFormat()
	{
		return bypassAutoFormat;
	}
	
	public boolean isRemoveTimeSuffix()
	{
		return removeTimeSuffix;
	}
	
	public void setTextTime(String textTime)
	{
		this.textTime = textTime;
	}
	
	public void setInterval(int interval)
	{
		this.interval = interval;
	}
	
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
	
	public Entry send()
	{
		if(playerScoreboard.getEntries().size() + playerScoreboard.getWrappers().size() < 15)
		{
			setup();
			paused = false;
			if(!(this instanceof Wrapper))
			{
				if(!(playerScoreboard.getEntries().contains(this)))
				{
					playerScoreboard.getEntries().add(this);
				}
			}else
			{
				if(!(playerScoreboard.getWrappers().contains(this)))
				{
					playerScoreboard.getWrappers().add((Wrapper) this);
				}
			}
		}
		return this;
	}

	public void sendScoreboardUpdate(String text)
	{
		Objective objective = playerScoreboard.getObjective();

		if(text.length() > 16)
		{
			team.setPrefix(text.substring(0, 16));

			String suffix = ChatColor.getLastColors(team.getPrefix()) + text.substring(16, text.length());

			if(suffix.length() > 16)
			{

				if(suffix.length() - 2 <= 16)
				{
					suffix = text.substring(16, text.length());
					team.setSuffix(suffix.substring(0, suffix.length()));
				}else
				{
					team.setSuffix(suffix.substring(0, 16));
				}
			}else
			{
				team.setSuffix(suffix);
			}
		}else
		{
			team.setPrefix(text);
		}

		Score score = objective.getScore(key);
		score.setScore(playerScoreboard.getScore(this));

		playerScoreboard.getPlayer().setScoreboard(playerScoreboard.getScoreboard());
	}

	public void setup()
	{
		Scoreboard scoreboard = playerScoreboard.getScoreboard();

		text = ChatColor.translateAlternateColorCodes('&', text);
		key = playerScoreboard.getAssignedKey(this);

		String teamName = id;
		if(teamName.length() > 16)
		{
			teamName = teamName.substring(0, 16);
		}

		if(scoreboard.getTeam(teamName) != null)
		{
			team = scoreboard.getTeam(teamName);
		}else
		{
			team = scoreboard.registerNewTeam(teamName);
		}
		if(!(team.getPlayers().contains(key)))
		{
			team.addPlayer(Bukkit.getPlayer(key));
		}
	}

	private boolean isValid()
	{
		if(text == null)
		{
			throw new NullPointerException("Entry text not defined!");
		}
		
		if(text.length() > 32)
		{
			throw new StringIndexOutOfBoundsException("Entry text must be equal to or below 32 characters long!");
		}
		
		return true;
	}

	public Entry setTime(double time)
	{
		this.time = BigDecimal.valueOf(time);
		return this;
	}

	public Entry setText(String text)
	{
		this.text = text;

		if(!(set))
		{
			set = true;
			originalText = text;
		}

		return this;
	}

	public Entry setTime(BigDecimal time)
	{
		this.time = time;
		return this;
	}

	public void cancel()
	{
		setCancelled(true);
	}
}
