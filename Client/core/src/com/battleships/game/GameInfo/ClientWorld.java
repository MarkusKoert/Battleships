package com.battleships.game.GameInfo;

import ClientConnection.ClientConnection;
import com.battleships.game.BodyFactory;

import java.util.HashMap;

public class ClientWorld {

    private ClientConnection clientConnection;
    private HashMap<Integer, BodyFactory> players = new HashMap<>(); // {id, player}

    public HashMap<Integer, BodyFactory> getPlayers() {
        return players;
    }

    public void addPlayer(int id, BodyFactory player) {
        players.put(id, player);
    }

    public void removePlayer(int id) {
        players.remove(id);
    }


/*
    public void addEnemy(int id, EnemyAI enemyAI) {
        enemyAIList.put(id, enemyAI);
    }

    public void removeEnemy(int id) {
        enemyAIList.remove(id);
    }

    public HashMap<Integer, EnemyAI> getEnemyAIList() {
        return enemyAIList;
    }

    public Set<Integer> getEnemyAIListIds() {
        return enemyAIList.keySet();
    }*/

    public ClientConnection getClientConnection() {
        return clientConnection;
    }

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

}
