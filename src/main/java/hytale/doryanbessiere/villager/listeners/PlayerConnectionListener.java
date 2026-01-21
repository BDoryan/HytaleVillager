package hytale.doryanbessiere.villager.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import hytale.doryanbessiere.villager.services.PlayerService;
import hytale.doryanbessiere.villager.utils.event.OnEvent;

public class PlayerConnectionListener {

    @OnEvent(PlayerConnectEvent.class)
    public void onPlayerConnect(PlayerConnectEvent event) {
        PlayerService.updatePlayer(event.getPlayerRef());
    }

    @OnEvent(PlayerDisconnectEvent.class)
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        PlayerService.updatePlayer(event.getPlayerRef());
    }
}
