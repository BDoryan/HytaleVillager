package hytale.doryanbessiere.villager.utils.hytale.entity;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;

import java.util.Map;
import java.util.UUID;

public class NpcUtils {

    public static NPCEntity getNpcEntityById(UUID entityId) {
        for(Map.Entry<String, World> entries : Universe.get().getWorlds().entrySet()) {
            World world = entries.getValue();

            Store<EntityStore> entityStore = world.getEntityStore().getStore();
            Ref<EntityStore> entityRef = world.getEntityRef(entityId);
            if(entityRef != null && entityRef.isValid()) {
                NPCEntity npcEntity = entityStore.getComponent(entityRef, NPCEntity.getComponentType());
                return npcEntity;
            }
        }
        return null;
    }

    public static void deleteEntityById(UUID entityId) {
        NPCEntity npcEntity = getNpcEntityById(entityId);
        npcEntity.remove();
    }
}
