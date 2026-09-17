package com.pixelpennant.engine;

import java.util.List;

public record LineScore(List<Integer> away, List<Integer> home, int awayHits, int homeHits) {
    public LineScore { away = List.copyOf(away); home = List.copyOf(home); }
    public int innings() { return Math.max(away.size(), home.size()); }
}

