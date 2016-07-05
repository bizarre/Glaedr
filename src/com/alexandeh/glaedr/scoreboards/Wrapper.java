package com.alexandeh.glaedr.scoreboards;

/**
 * Copyright © 2016 Alexander Maxwell Use and or redistribution of compiled JAR
 * file and or source code is permitted only if given explicit permission from
 * original author: Alexander Maxwell
 */
public class Wrapper extends Entry
{
	private WrapperType type;

	public Wrapper(String id, PlayerScoreboard playerScoreboard, WrapperType type)
	{
		super(id, playerScoreboard);
		this.type = type;
	}
	
	public WrapperType getType()
	{
		return type;
	}
	
	public enum WrapperType
	{
		TOP, BOTTOM
	}
}
