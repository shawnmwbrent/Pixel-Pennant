package com.pixelpennant.engine;

/** Stable, persistence-friendly ratings for future modes. */
public record PlayerRatings(int contact, int power, int speed, int fielding, int pitching) {
    public PlayerRatings {
        validate(contact); validate(power); validate(speed); validate(fielding); validate(pitching);
    }
    private static void validate(int value) {
        if (value < 0 || value > 100) throw new IllegalArgumentException("Ratings must be 0..100");
    }
}

