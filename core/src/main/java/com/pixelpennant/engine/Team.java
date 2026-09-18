package com.pixelpennant.engine;

import java.util.List;

import java.util.Objects;

public final class Team {
    private final String id;
    private final String city;
    private final String name;
    private final List<Player> roster;

    public Team(String id, String city, String name, List<Player> roster) {
        this.id = Objects.requireNonNull(id);
        this.city = Objects.requireNonNull(city);
        this.name = Objects.requireNonNull(name);
        this.roster = List.copyOf(roster);
    }

    public String id() { return id; }
    public String city() { return city; }
    public String name() { return name; }
    public List<Player> roster() { return roster; }
    public String displayName() { return city + " " + name; }

    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Team that)) return false;
        return id.equals(that.id) && city.equals(that.city) && name.equals(that.name) && roster.equals(that.roster);
    }

    @Override public int hashCode() { return Objects.hash(id, city, name, roster); }
}
