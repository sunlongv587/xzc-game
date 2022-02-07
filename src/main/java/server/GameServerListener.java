package server;

import java.util.EventListener;

public interface GameServerListener extends EventListener {
    void serverEvent(GameServerEvent evt);
}
