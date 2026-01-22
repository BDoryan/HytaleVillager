package hytale.doryanbessiere.villager.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import hytale.doryanbessiere.villager.dto.PlayerData;
import hytale.doryanbessiere.villager.services.PlayerService;
import hytale.doryanbessiere.villager.utils.hytale.event.OnEvent;

public class PlayerConnectionListener {

    @OnEvent(PlayerConnectEvent.class)
    public void onPlayerConnect(PlayerConnectEvent event) {
        PlayerData playerData = PlayerService.findPlayerDataOrCreate(event.getPlayerRef());
    }

//    @OnEvent(PlayerDisconnectEvent.class)
//    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
//    }
}
