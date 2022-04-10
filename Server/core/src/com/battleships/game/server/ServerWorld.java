package com.battleships.game.server;

import Packets.PacketAddPlayer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ServerWorld {
    private Map<Integer, PacketAddPlayer> players = new LinkedHashMap<>();

    public void addPlayer(Integer id, PacketAddPlayer addPlayer) {
        players.put(id, addPlayer);
    }


    public void removeId(int id) {
        players.remove(id);
    }

    public Set<Integer> getConnectedIds() {
        return players.keySet();
    }

    public Map<Integer, PacketAddPlayer> getPlayers() {
        return players;
    }


}
