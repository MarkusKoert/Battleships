package com.battleships.game.gameinfo;

import ClientConnection.ClientConnection;
import com.badlogic.ashley.core.Entity;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientWorld {

    private ClientConnection clientConnection;
    private Map<Integer, Entity> players =  new LinkedHashMap<>(); // {id, player}

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
}