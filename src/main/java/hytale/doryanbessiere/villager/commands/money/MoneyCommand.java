package hytale.doryanbessiere.villager.commands.money;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import hytale.doryanbessiere.villager.dto.PlayerData;
import hytale.doryanbessiere.villager.services.PlayerService;
import hytale.doryanbessiere.villager.utils.Utils;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * /money | To get your balance (player only)
 * /money update <amount(+10/-10/500)> <player> | To set a player's balance (admin only)
 */
public class MoneyCommand extends AbstractPlayerCommand {

    private static long getBalance(UUID uuid) {
        PlayerData playerData = PlayerService.getPlayerData(uuid);
        return playerData.getBalance();
    }

    public MoneyCommand() {
        super("money", "Manage player money balances");

        this.addUsageVariant(new MoneyOtherCommand());
        this.addSubCommand(new MoneyUpdateCommand());
    }

    /**
     * This method is called when you type "/money" to get your own balance
     */
    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        long balance = getBalance(playerRef.getUuid());
        context.sender().sendMessage(Message.raw("Your balance is: " + balance + " coins."));
    }

    class MoneyUpdateCommand extends CommandBase {

        private final RequiredArg<String> amountArg = this.withRequiredArg("amount", "The amount to update the balance by", ArgTypes.STRING);
        private final RequiredArg<PlayerRef> targetPlayerArg = this.withRequiredArg("player", "The player whose balance to update", ArgTypes.PLAYER_REF);

        public MoneyUpdateCommand() {
            super("update", "Update another player's money balance");
        }

        /**
         * Execute the command to update another player's balance
         */
        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            PlayerRef targetPlayerRef = (PlayerRef) this.targetPlayerArg.get(context);
            String amountString = this.amountArg.get(context);
            long amount = 0;
            boolean absolute = false;
            if (amountString.startsWith("+")) {
                amount = Long.parseLong(amountString.substring(1));
            } else if (amountString.startsWith("-")) {
                amount = -Long.parseLong(amountString.substring(1));
            } else if (Utils.isLong(amountString)) {
                amount = Long.parseLong(amountString);
                absolute = true;
            } else {
                context.sender().sendMessage(Message.raw("Invalid amount: " + amountString));
                return;
            }


            PlayerData playerData = PlayerService.getPlayerData(targetPlayerRef);
            playerData.setBalance(Long.max(0, absolute ? amount : playerData.getBalance() + amount));
            PlayerService.savePlayerData(playerData);

            context.sender().sendMessage(Message.raw("Updated " + targetPlayerRef.getUsername() + "'s balance by " + amount + " coins."));
        }
    }

    class MoneyOtherCommand extends CommandBase {

        private final RequiredArg<PlayerRef> targetPlayerArg = this.withRequiredArg("player", "The player whose balance to check", ArgTypes.PLAYER_REF);

        public MoneyOtherCommand() {
            super("Check another player's money balance");
        }

        /**
         * Execute the command to check another player's balance
         */
        @Override
        protected void executeSync(@Nonnull CommandContext context) {
            PlayerRef targetPlayerRef = (PlayerRef) this.targetPlayerArg.get(context);
            UUID targetUuid = targetPlayerRef.getUuid();

            long balance = getBalance(targetUuid);
            context.sender().sendMessage(Message.raw("Player " + targetPlayerRef.getUsername() + " has a balance of: " + balance + " coins."));
        }
    }
}
