package com.pixelpennant.engine;

import java.util.Objects;

public final class Player {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final Position position;
    private final PlayerRatings ratings;

    public Player(String id, String firstName, String lastName, Position position, PlayerRatings ratings) {
        Objects.requireNonNull(id); Objects.requireNonNull(firstName); Objects.requireNonNull(lastName);
        Objects.requireNonNull(position); Objects.requireNonNull(ratings);
        if (id.isBlank()) throw new IllegalArgumentException("Player id is required");
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.ratings = ratings;
    }

    public String id() { return id; }
    public String firstName() { return firstName; }
    public String lastName() { return lastName; }
    public Position position() { return position; }
    public PlayerRatings ratings() { return ratings; }
    public String displayName() { return firstName + " " + lastName; }

    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Player that)) return false;
        return id.equals(that.id) && firstName.equals(that.firstName) && lastName.equals(that.lastName)
                && position == that.position && ratings.equals(that.ratings);
    }

    @Override public int hashCode() { return Objects.hash(id, firstName, lastName, position, ratings); }
}
