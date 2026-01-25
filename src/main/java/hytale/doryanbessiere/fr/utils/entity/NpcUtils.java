package hytale.doryanbessiere.fr.utils.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import hytale.doryanbessiere.fr.villager.HytaleVillager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class NpcUtils {

    /**
     * Get NPC entity by its UUID across all worlds (async).
     *
     * @param entityId
     * @return
     */
    public static CompletableFuture<NPCEntity> getNpcEntityByIdAsync(UUID entityId) {
        HytaleVillager.logger().atInfo().log("Starting search for NPC entity with ID: " + entityId);
        CompletableFuture<NPCEntity> future = new CompletableFuture<>();

        Map<String, World> worlds = Universe.get().getWorlds();
        if (worlds.isEmpty()) {
            future.complete(null);
            return future;
        }

        HytaleVillager.logger().atInfo().log("Searching for NPC entity with ID: " + entityId + " across all worlds.");
        AtomicInteger remaining = new AtomicInteger(worlds.size());
        for (Map.Entry<String, World> entries : worlds.entrySet()) {
            HytaleVillager.logger().atInfo().log("Checking world: " + entries.getKey());
            World world = entries.getValue();

            HytaleVillager.logger().atInfo().log("Executing search in world: " + entries.getKey());
            world.execute(() -> {
                if (future.isDone()) {
                    return;
                }
                Store<EntityStore> entityStore = world.getEntityStore().getStore();
                Ref<EntityStore> entityRef = world.getEntityRef(entityId);
                if (entityRef != null && entityRef.isValid()) {
                    HytaleVillager.logger().atInfo().log("Found NPC entity with ID: " + entityId + " in world: " + entries.getKey());
                    NPCEntity npcEntity = entityStore.getComponent(entityRef, NPCEntity.getComponentType());
                    future.complete(npcEntity);
                } else {
                    HytaleVillager.logger().atInfo().log("NPC entity with ID: " + entityId + " not found in world: " + entries.getKey());
                    if (remaining.decrementAndGet() == 0) {
                        future.complete(null);
                    }
                }
            });
        }

        return future;
    }

    /**
     * Get NPC entity by its UUID across all worlds (blocking).
     * Avoid calling this from a world thread.
     *
     * @param entityId
     * @return
     */
    public static NPCEntity getNpcEntityById(UUID entityId) {
        return getNpcEntityByIdAsync(entityId).join();
    }

    /**
     * Delete NPC entity by its UUID.
     *
     * @param entityId
     */
    public static void deleteEntityById(UUID entityId) {
        getNpcEntityByIdAsync(entityId).thenAccept(npcEntity -> {
            if (npcEntity == null) {
                return;
            }
            World world = npcEntity.getWorld();
            world.execute(npcEntity::remove);
        });
    }
}
