package hytale.doryanbessiere.fr.utils.entity;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.*;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.support.EntitySupport;

import java.util.UUID;

/**
 * Builder class for creating and spawning NPC entities in the game world.
 *
 * @author Doryan BESSIERE
 * @website https://www.doryanbessiere.fr
 */
public class NpcBuilder {

    private final HytaleLogger logger = HytaleLogger.forEnclosingClass();

    protected final Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();

    protected World world;

    protected Vector3d position;
    protected Vector3f rotation = new Vector3f(0, 0, 0);

    protected ModelAsset modelAsset;
    protected Model model;

    protected float scale = 1.0f;

    protected boolean interactive = false;
    protected boolean persistent = false;
    protected boolean invulnerable = true;

    protected String roleName = null;
    protected String displayName = null;

    /**
     * Create a new NPC builder with the given model asset name.
     *
     * @param modelAssetName
     * @return
     */
    public static NpcBuilder create(String modelAssetName, World world, Vector3d position) {
        NpcBuilder builder = new NpcBuilder(modelAssetName);
        builder.world(world);
        builder.position(position);

        return builder;
    }

    public NpcBuilder(String modelAssetName) {
        this.modelAsset = ModelAsset.getAssetMap().getAsset(modelAssetName);
    }

    /**
     * Builds the NPC entity.
     *
     * @return
     */
    public NpcBuilder build() {
        this.model = Model.createScaledModel(this.modelAsset, this.scale);

        Store<EntityStore> store = world.getEntityStore().getStore();

        NPCEntity npcEntity = null;
        if (this.roleName != null) {
            int roleIndex = NPCPlugin.get().getIndex(this.roleName);

            npcEntity = new NPCEntity();
            npcEntity.setRoleName(this.roleName);
            npcEntity.setRoleIndex(roleIndex);

            this.holder.addComponent(NPCEntity.getComponentType(), npcEntity);
        }

        this.holder.ensureComponent(UUIDComponent.getComponentType());
        this.holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(this.position, this.rotation));
        this.holder.addComponent(HeadRotation.getComponentType(), new HeadRotation(this.rotation));
        this.holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(this.model));
        this.holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        this.holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(this.model.getBoundingBox()));

        if (this.displayName != null) {
            this.holder.addComponent(DisplayNameComponent.getComponentType(),
                    new DisplayNameComponent(Message.raw(this.displayName)));
        }

        if (this.invulnerable)
            this.holder.ensureComponent(Invulnerable.getComponentType());

        if (this.persistent)
            this.holder.addComponent(PersistentModel.getComponentType(), new PersistentModel(this.model.toReference()));

        if (this.interactive) {
            this.holder.addComponent(Interactions.getComponentType(), new Interactions());
            this.holder.ensureComponent(Interactable.getComponentType());
        }

        return this;
    }

    /**
     * Spawns the NPC in the world.
     */
    public UUID spawn() {
        Store<EntityStore> store = world.getEntityStore().getStore();

        world.execute(() -> {
            Ref<EntityStore> ref = store.addEntity(this.holder, AddReason.SPAWN);

            if (ref != null && this.displayName != null) {
                EntitySupport.setDisplayName(ref, this.displayName, true, store);
                logger.atInfo().log("Set NPC display name to: " + this.displayName);
            }
        });

        return this.holder.getComponent(UUIDComponent.getComponentType()).getUuid();
    }

    /**
     * Sets the role name of the NPC.
     *
     * @param role
     * @return
     */
    public NpcBuilder roleName(String role) {
        this.roleName = role;
        return this;
    }

    /**
     * Sets whether the NPC is interactive.
     *
     * @param interactive
     * @return
     */
    public NpcBuilder interactive(boolean interactive) {
        this.interactive = interactive;
        return this;
    }

    /**
     * Display name of the NPC.
     *
     * @param displayName
     * @return
     */
    public NpcBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Sets whether the NPC is persistent.
     *
     * @param persistent
     * @return
     */
    public NpcBuilder persistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    /**
     * Sets the world where the NPC will be spawned.
     *
     * @param world
     * @return
     */
    public NpcBuilder world(World world) {
        this.world = world;
        return this;
    }

    /**
     * Sets the position where the NPC will be spawned.
     *
     * @param position
     * @return
     */
    public NpcBuilder position(Vector3d position) {
        this.position = position;
        return this;
    }

    /**
     * Sets the rotation of the NPC.
     *
     * @param rotation
     * @return
     */
    public NpcBuilder rotation(Vector3f rotation) {
        this.rotation = rotation;
        return this;
    }

    /**
     * Sets the scale of the NPC.
     *
     * @param scale
     * @return
     */
    public NpcBuilder scale(float scale) {
        this.scale = scale;
        return this;
    }

    public Holder<EntityStore> getHolder() {
        return holder;
    }
}
