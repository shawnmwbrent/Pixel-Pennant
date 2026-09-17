package com.pixelpennant.engine;

import java.util.Objects;

public record Player(String id, String firstName, String lastName, Position position,
                     PlayerRatings ratings) {
    public Player {
        Objects.requireNonNull(id); Objects.requireNonNull(firstName); Objects.requireNonNull(lastName);
        Objects.requireNonNull(position); Objects.requireNonNull(ratings);
        if (id.isBlank()) throw new IllegalArgumentException("Player id is required");
    }
    public String displayName() { return firstName + " " + lastName; }
}

