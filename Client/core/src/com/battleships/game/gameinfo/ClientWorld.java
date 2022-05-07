package com.battleships.game.gameinfo;

import ClientConnection.ClientConnection;
import com.badlogic.ashley.core.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class stores data needed for multiplayer.
 */
public class ClientWorld {

    private ClientConnection clientConnection;
    private Map<Integer, Entity> players =  new LinkedHashMap<>(); // {id, player}

    private Map<Integer, Entity> lootMap =  new LinkedHashMap<>(); // {id, loot}

    public Map<Integer, Entity> getPlayers() {
        return players;
    }

    public void addPlayer(int id, Entity player) {
        players.put(id, player);
    }

    public void removePlayer(int id) {
        players.remove(id);
    }

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    public int getThisClientId() {
        return clientConnection.getThisClientId();
    }

    public Map<Integer, Entity> getLoot() {
        return lootMap;
    }

    public void addLoot(int id, Entity loot) {
        lootMap.put(id, loot);
    }

    public void removeLoot(int id) {
        lootMap.remove(id);
    }

}
