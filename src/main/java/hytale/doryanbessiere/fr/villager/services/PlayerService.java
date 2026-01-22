package hytale.doryanbessiere.fr.villager.services;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import hytale.doryanbessiere.fr.villager.exceptions.player.PlayerNotFoundException;
import hytale.doryanbessiere.fr.villager.repository.adapter.file.PlayerRepositoryFile;
import hytale.doryanbessiere.fr.villager.dto.PlayerData;
import hytale.doryanbessiere.fr.villager.repository.PlayerRepository;

import java.util.UUID;

public class PlayerService {

    private static final PlayerRepository playerRepository = new PlayerRepositoryFile();

    /**
     * Update or create a player data entry in the repository.
     *
     * @param playerRef
     */
    public static PlayerData findPlayerDataOrCreate(PlayerRef playerRef) {
        PlayerData playerData = playerRepository.findById(playerRef.getUuid());
        if(playerData == null) {
            playerData = new PlayerData(playerRef);
            savePlayerData(playerData);
        }
        return playerData;
    }

    public static void savePlayerData(PlayerData playerData) {
        playerRepository.set(playerData);
    }

    /**
     * Return the player data by UUID
     *
     * @param uuid the UUID of the player
     * @return
     * @throws PlayerNotFoundException if the player is not found
     */
    public static PlayerData getPlayerData(UUID uuid) {
        PlayerData playerData = playerRepository.findById(uuid);
        if(playerData == null)
            throw new PlayerNotFoundException(uuid);

        return playerData;
    }

    /**
     * Return the player data by PlayerRef
     *
     * @param playerRef the player reference of Hytale
     * @return
     * @throws PlayerNotFoundException if the player is not found
     */
    public static PlayerData getPlayerData(PlayerRef playerRef) {
        return getPlayerData(playerRef.getUuid());
    }
}
