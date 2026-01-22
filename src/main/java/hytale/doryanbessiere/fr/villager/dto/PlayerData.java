package hytale.doryanbessiere.fr.villager.dto;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import hytale.doryanbessiere.fr.villager.dto.economy.BalanceData;

public class PlayerData extends BalanceData {

    private final String id;
    private final String username;

    public PlayerData(PlayerRef player) {
        this.id = player.getUuid().toString();
        this.username = player.getUsername();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
