package com.pixelpennant.engine;

import java.util.List;

public record Team(String id, String city, String name, List<Player> roster) {
    public Team { roster = List.copyOf(roster); }
    public String displayName() { return city + " " + name; }
}

