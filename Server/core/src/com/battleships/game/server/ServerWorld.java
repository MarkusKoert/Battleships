package com.battleships.game.server;

import Packets.PacketAddPlayer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ServerWorld {
    private Map<Integer, PacketAddPlayer> players = new HashMap<>();
    private final Point[] lootCoordinates = {
            new Point(83, 25),
            new Point(18, 169),
            new Point(99, 229),
            new Point(198, 330),
            new Point(334, 334),
            new Point(309, 177),
            new Point(324, 29),
            new Point(269, 6),
            new Point(188, 95),
            new Point(131, 5),
            new Point(169, 169),
            new Point(171, 221),
            new Point(255, 209),
            new Point(227, 125),
            new Point(186, 104),
            new Point(185, 130)
    };

    /**
     * Add a player to server map
     * @param id - player ID
     * @param addPlayer - player add packet
     */
    public void addPlayer(Integer id, PacketAddPlayer addPlayer) {
        players.put(id, addPlayer);
    }

    /**
     * Add player to server map
     * @param id - player ID
     */
    public void removePlayer(int id) {
        players.remove(id);
    }

    public Point[] getLootCoordinates() {
        return lootCoordinates;
    }

    public Map<Integer, PacketAddPlayer> getPlayers() {
        return players;
    }
}
