package hytale.doryanbessiere.fr.villager.exceptions.player;

import java.util.UUID;

public class PlayerNotFoundException extends PlayerException {

    private final UUID id;

    public PlayerNotFoundException(UUID id) {
        super("Player with UUID " + id.toString() + " not found.");
        this.id = id;
    }

    public UUID getUuid() {
        return id;
    }
}
