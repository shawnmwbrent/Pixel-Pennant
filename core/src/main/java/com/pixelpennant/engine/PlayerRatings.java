package com.pixelpennant.engine;

import java.util.Objects;

/** Stable, persistence-friendly ratings for future modes. */
public final class PlayerRatings {
    private final int contact;
    private final int power;
    private final int speed;
    private final int fielding;
    private final int pitching;

    public PlayerRatings(int contact, int power, int speed, int fielding, int pitching) {
        validate(contact); validate(power); validate(speed); validate(fielding); validate(pitching);
        this.contact = contact;
        this.power = power;
        this.speed = speed;
        this.fielding = fielding;
        this.pitching = pitching;
    }

    public int contact() { return contact; }
    public int power() { return power; }
    public int speed() { return speed; }
    public int fielding() { return fielding; }
    public int pitching() { return pitching; }

    private static void validate(int value) {
        if (value < 0 || value > 100) throw new IllegalArgumentException("Ratings must be 0..100");
    }

    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof PlayerRatings that)) return false;
        return contact == that.contact && power == that.power && speed == that.speed
                && fielding == that.fielding && pitching == that.pitching;
    }

    @Override public int hashCode() { return Objects.hash(contact, power, speed, fielding, pitching); }

    @Override public String toString() {
        return "PlayerRatings[contact=" + contact + ", power=" + power + ", speed=" + speed
                + ", fielding=" + fielding + ", pitching=" + pitching + "]";
    }
}
