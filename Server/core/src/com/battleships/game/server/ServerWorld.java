package com.battleships.game.server;

import Packets.PacketAddPlayer;

import java.util.HashMap;
import java.util.Map;

public class ServerWorld {
    private Map<Integer, PacketAddPlayer> players = new HashMap<>();

    public void addPlayer(Integer id, PacketAddPlayer addPlayer) {
        players.put(id, addPlayer);
    }

    public void removePlayer(int id) {
        players.remove(id);
    }

    public Map<Integer, PacketAddPlayer> getPlayers() {
        return players;
    }
}
