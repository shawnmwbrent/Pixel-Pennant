package com.pixelpennant.engine;

import java.util.ArrayList;
import java.util.List;

public final class Teams {
    private Teams() {}
    public static Team toronto() { return make("tor", "Toronto", "Bluebirds", 73, 71); }
    public static Team detroit() { return make("det", "Detroit", "Motors", 68, 70); }

    private static Team make(String id, String city, String name, int bat, int pitch) {
        String[] first = {"Avery","Maya","Jules","Rowan","Kai","Noa","Remy","Quinn","Sage"};
        String[] last = id.equals("tor")
                ? new String[]{"Finch","Harbour","Wren","Maple","Skye","Reed","Lake","Swift","North"}
                : new String[]{"Axel","Piston","Mercer","Steel","Lane","Spark","Miles","Rivet","Ford"};
        Position[] positions = Position.values();
        List<Player> roster = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            roster.add(new Player(id + "-" + (i + 1), first[i], last[i], positions[i],
                    new PlayerRatings(bat + (i % 4) - 2, 62 + (i * 3) % 18,
                            65 + (i * 5) % 20, 68 + (i * 2) % 17, i == 0 ? pitch : 35)));
        }
        return new Team(id, city, name, roster);
    }
}

