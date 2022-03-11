package com.battleships.game.server;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

public class ServerWorld {
    private HashMap<Integer, String> players = new LinkedHashMap<>();

    public void addPlayer(int id, String name) {
        players.put(id, name);
    }

    public void removeId(int id) {
        players.remove(id);
    }

    public Set<Integer> getConnectedIds() {
        return players.keySet();
    }

    public HashMap<Integer, String> getPlayers() {
        return players;
    }

}
