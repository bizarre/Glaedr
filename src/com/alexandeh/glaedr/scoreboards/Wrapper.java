package com.alexandeh.glaedr.scoreboards;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
@Getter
@Setter
@Accessors(fluent = false, chain = true)
public class Wrapper extends Entry {

    public enum WrapperType {
        TOP,
        BOTTOM
    }

    private WrapperType type;

    public Wrapper(String id, PlayerScoreboard playerScoreboard, WrapperType type) {
        super(id, playerScoreboard);

        this.type = type;
    }
}
