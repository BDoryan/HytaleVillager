package hytale.doryanbessiere.fr.villager.commands.debug;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import hytale.doryanbessiere.fr.villager.HytaleVillager;
import hytale.doryanbessiere.fr.utils.PlayerUtils;
import hytale.doryanbessiere.fr.utils.entity.NpcBuilder;
import hytale.doryanbessiere.fr.utils.entity.NpcUtils;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DebugCommand extends AbstractCommandCollection {

    public DebugCommand() {
        super("debug", "Debug commands");

        this.addSubCommand(new DebugNpcCollectionCommands());
    }

    class DebugNpcCollectionCommands extends AbstractCommandCollection {

        public DebugNpcCollectionCommands() {
            super("npc", "Debug NPC commands");

            this.addSubCommand(new DebugGetNpcCommand());
            this.addSubCommand(new DebugSpawnNpcCommand());
        }
    }

    class DebugSpawnNpcCommand extends AbstractAsyncPlayerCommand {

        public DebugSpawnNpcCommand() {
            super("spawn", "Debug NPC commands");
        }

        @Override
        protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
            Player player = PlayerUtils.getPlayer(playerRef);

            UUID uuid = NpcBuilder.create("Kweebec_Sapling", world, playerRef.getTransform().getPosition())
                    .rotation(playerRef.getTransform().getRotation())
                    .interactive(InteractionType.Use, "Root_Bank_Open")
                    .persistent(false)
                    .roleName("LookAtMe")
                    .displayName("Debug NPC")
                    .build()
                    .spawn();

            commandContext.sender().sendMessage(Message.raw("Spawned NPC with ID: " + uuid));
            HytaleVillager.logger().atInfo().log("Spawned NPC with ID: " + uuid);

            return CompletableFuture.completedFuture(null);
        }
    }

    class DebugGetNpcCommand extends AbstractAsyncCommand {

        private final RequiredArg<UUID> npcIdArg = this.withRequiredArg("npcId", "The NPC ID to debug", ArgTypes.UUID);

        public DebugGetNpcCommand() {
            super("get", "Debug NPC commands");
        }

        @Override
        protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext) {
            UUID npcId = npcIdArg.get(commandContext);

            commandContext.sender().sendMessage(Message.raw("Debugging NPC with ID: " + npcId));
            NPCEntity npcRef = NpcUtils.getNpcEntityById(npcId);
            if (npcRef != null) {
                commandContext.sender().sendMessage(Message.raw("Found NPC with ID: " + npcId));
                HytaleVillager.logger().atInfo().log("Found NPC with ID: " + npcId);
            } else {
                commandContext.sender().sendMessage(Message.raw("NPC with ID " + npcId + " not found."));
                HytaleVillager.logger().atInfo().log("NPC with ID " + npcId + " not found.");
            }

            return CompletableFuture.completedFuture(null);
        }
    }
}
