package hytale.doryanbessiere.fr.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class PlayerUtils {

    /**
     * Get player from PlayerRef.
     *
     * @param playerRef
     * @return
     */
    public static Player getPlayer(PlayerRef playerRef) {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref != null && ref.isValid()) {
            Store<EntityStore> store = ref.getStore();

            return store.getComponent(ref, Player.getComponentType());
        }
        return null;
    }
}
