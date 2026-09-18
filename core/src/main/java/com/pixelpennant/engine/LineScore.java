package com.pixelpennant.engine;

import java.util.List;
import java.util.Objects;

public final class LineScore {
    private final List<Integer> away;
    private final List<Integer> home;
    private final int awayHits;
    private final int homeHits;

    public LineScore(List<Integer> away, List<Integer> home, int awayHits, int homeHits) {
        this.away = List.copyOf(away);
        this.home = List.copyOf(home);
        this.awayHits = awayHits;
        this.homeHits = homeHits;
    }

    public List<Integer> away() { return away; }
    public List<Integer> home() { return home; }
    public int awayHits() { return awayHits; }
    public int homeHits() { return homeHits; }
    public int innings() { return Math.max(away.size(), home.size()); }

    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof LineScore that)) return false;
        return awayHits == that.awayHits && homeHits == that.homeHits
                && away.equals(that.away) && home.equals(that.home);
    }

    @Override public int hashCode() { return Objects.hash(away, home, awayHits, homeHits); }
}
