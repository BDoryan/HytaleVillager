package hytale.doryanbessiere.villager.repository.adapter.file;

import hytale.doryanbessiere.villager.dto.PlayerData;
import hytale.doryanbessiere.villager.repository.PlayerRepository;
import hytale.doryanbessiere.villager.utils.FileStorageManager;

import java.util.List;

public class PlayerRepositoryFile extends PlayerRepository {

    private final FileStorageManager<PlayerData> storage;

    public PlayerRepositoryFile() {
        this.storage = new FileStorageManager<>("hytale-villager/players/");
    }

    @Override
    public PlayerData findById(Object id) {
        return storage.load(id.toString() + ".json", PlayerData.class);
    }

    @Override
    public boolean existById(Object search) {
        return findById(search) != null;
    }

    @Override
    public void create(PlayerData data) {
        set(data);
    }

    public void set(PlayerData villagerPlayerData) {
        storage.save(villagerPlayerData, villagerPlayerData.getId() + ".json");
    }

    @Override
    public void delete(PlayerData data) {
        storage.delete(data.getId() + ".json");
    }

    @Override
    public List<PlayerData> findAll() {
        return storage.loadAll(PlayerData.class);
    }
}
