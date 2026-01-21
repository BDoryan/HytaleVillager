package hytale.doryanbessiere.villager.services;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import hytale.doryanbessiere.villager.repository.adapter.file.PlayerRepositoryFile;
import hytale.doryanbessiere.villager.dto.PlayerData;
import hytale.doryanbessiere.villager.repository.PlayerRepository;

public class PlayerService {

    private static final PlayerRepository playerRepository = new PlayerRepositoryFile();

    /**
     * Update or create a player data entry in the repository.
     *
     * @param playerRef
     */
    public static void updatePlayer(PlayerRef playerRef) {
        if(!playerRepository.existById(playerRef.getUuid().toString())) {
            playerRepository.create(new PlayerData(playerRef));
            return;
        }
        playerRepository.set(new PlayerData(playerRef));
    }

    /**
     * Return the player data by uuid
     *
     * @param uuid
     * @return
     */
    public static PlayerData getPlayerData(String uuid) {
        return playerRepository.findById(uuid);
    }

    /**
     * Return the player data by PlayerRef
     *
     * @param playerRef
     * @return
     */
    public static PlayerData getPlayerData(PlayerRef playerRef) {
        return getPlayerData(playerRef.getUuid().toString());
    }
}
