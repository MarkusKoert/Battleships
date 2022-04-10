package com.battleships.game.entity.components;

import com.badlogic.ashley.core.Component;
import ClientConnection.ClientConnection;

public class ConnectionComponent implements Component {

    private ClientConnection clientConnection;

    public void setClientConnection(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }
}
