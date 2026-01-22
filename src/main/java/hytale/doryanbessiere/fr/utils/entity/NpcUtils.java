package hytale.doryanbessiere.fr.utils.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NpcUtils {

    /**
     * Get NPC entity by its UUID across all worlds.
     *
     * @param entityId
     * @return
     */
    public static NPCEntity getNpcEntityById(UUID entityId) {
        CompletableFuture<NPCEntity> future = new CompletableFuture<>();

        for(Map.Entry<String, World> entries : Universe.get().getWorlds().entrySet()) {
            World world = entries.getValue();

            world.execute(() -> {
                Store<EntityStore> entityStore = world.getEntityStore().getStore();
                Ref<EntityStore> entityRef = world.getEntityRef(entityId);
                if(entityRef != null && entityRef.isValid()) {
                    NPCEntity npcEntity = entityStore.getComponent(entityRef, NPCEntity.getComponentType());
                    future.complete(npcEntity);
                }
            });
        }

        return future.join();
    }

    /**
     * Delete NPC entity by its UUID.
     *
     * @param entityId
     */
    public static void deleteEntityById(UUID entityId) {
        NPCEntity npcEntity = getNpcEntityById(entityId);
        World world = npcEntity.getWorld();
        world.execute(npcEntity::remove);
    }
}
