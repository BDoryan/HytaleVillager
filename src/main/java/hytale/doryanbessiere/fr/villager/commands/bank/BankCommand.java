package hytale.doryanbessiere.fr.villager.commands.bank;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankType;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankNameAlreadyExistsException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankNotFoundException;
import hytale.doryanbessiere.fr.villager.services.bank.BankService;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * # Bank commands
 * - /bank list
 * - /bank info <name>
 * - /bank create <name> <type>
 * - /bank delete <name>
 */
public class BankCommand extends AbstractCommandCollection {

    private final RequiredArg<String> listArg = this.withRequiredArg("list", "List your bank accounts", ArgTypes.STRING);

    /**
     * This command is created for bank management
     */
    public BankCommand() {
        super("bank", "Check your bank balance");

        // Bank commands
        this.addSubCommand(new BankInfoCommand());
        this.addSubCommand(new BankCreateCommand());
        this.addSubCommand(new BankDeleteCommand());

        // Account commands
        this.addSubCommand(new BankAccountCommand());
    }

    class BankListCommand extends AbstractAsyncCommand {

        /**
         * This command is created for list all banks
         */
        public BankListCommand() {
            super("list", "List your bank accounts");
        }

        @NonNullDecl
        protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
            CommandSender sender = commandContext.sender();

            List<BankData> banks = BankService.getBanks();
            sender.sendMessage(Message.raw("List of banks on the server:"));
            if (banks.isEmpty()) {
                sender.sendMessage(Message.raw(" * No banks found."));
                return CompletableFuture.completedFuture(null);
            }

            for (BankData bank : banks) {
                sender.sendMessage(Message.raw(" - Bank: " + bank.getName() + " | Type: " + bank.getType() + " | Balance: " + bank.getTotalBalance()));
            }

            return CompletableFuture.completedFuture(null);
        }
    }

    class BankDeleteCommand extends AbstractAsyncCommand {

        private final RequiredArg<String> bankNameArg = this.withRequiredArg("name", "Name of the bank to delete", ArgTypes.STRING);

        /**
         * This command is created for delete a bank
         */
        public BankDeleteCommand() {
            super("delete", "Delete a bank");
        }

        @Override
        protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext) {
            CommandSender sender = commandContext.sender();
            String bankName = this.bankNameArg.get(commandContext);
            try {
                BankService.deleteBank(bankName);
                sender.sendMessage(Message.raw("Bank with the name '" + bankName + "' has been deleted successfully."));
            } catch (BankNotFoundException e) {
                sender.sendMessage(Message.raw("Bank with the name '" + bankName + "' does not exist."));
            }

            return CompletableFuture.completedFuture(null);
        }
    }

    class BankInfoCommand extends AbstractAsyncCommand {

        private final RequiredArg<String> bankNameArg = this.withRequiredArg("name", "Name of the bank", ArgTypes.STRING);

        /**
         * This command is created for get information about a bank
         */
        public BankInfoCommand() {
            super("info", "Get information about a bank");
        }

        @Override
        protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext) {
            CommandSender sender = commandContext.sender();
            String bankName = this.bankNameArg.get(commandContext);
            try {
                BankData bankData = BankService.getBankByName(bankName);

                sender.sendMessage(Message.raw("--[ Bank Information ]--"));
                sender.sendMessage(Message.raw("Name: " + bankData.getName()));
                sender.sendMessage(Message.raw("Type: " + bankData.getType()));
                sender.sendMessage(Message.raw("Total Accounts: " + bankData.getTotalAccounts()));
                sender.sendMessage(Message.raw("Total Balance: " + bankData.getTotalBalance()));
                sender.sendMessage(Message.raw("-----------------------"));
            } catch (Exception e) {
                sender.sendMessage(Message.raw("Bank with the name '" + bankName + "' does not exist."));
            }

            return CompletableFuture.completedFuture(null);
        }
    }

    class BankCreateCommand extends AbstractAsyncPlayerCommand {

        private final RequiredArg<String> nameArg = this.withRequiredArg("name", "Name of the bank", ArgTypes.STRING);
        private final RequiredArg<String> typeArg = this.withRequiredArg("type", "Type of the bank (VILLAGE or GOVERNMENT)", ArgTypes.STRING);

        /**
         * This command is created for create new bank
         */
        public BankCreateCommand() {
            super("create", "Create a new bank");
        }

        @Override
        protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
            CommandSender sender = commandContext.sender();

            String name = this.nameArg.get(commandContext);
            String typeStr = this.typeArg.get(commandContext);

            try {
                BankType type = BankType.valueOf(typeStr.toUpperCase());

                try {
                    BankService.createBank(name, type, playerRef);

                    sender.sendMessage(Message.raw("Bank '" + name + "' of type '" + type + "' created successfully."));
                } catch (BankNameAlreadyExistsException e) {
                    sender.sendMessage(Message.raw("A bank with the name '" + name + "' already exists."));
                }
            } catch (IllegalArgumentException e) {
                String enumerationsString = String.join(", ", Arrays
                        .stream(BankType.values())
                        .map(Enum::name)
                        .toArray(String[]::new)
                );
                sender.sendMessage(Message.raw("Invalid bank type. Use " + enumerationsString + "."));
            }
            return CompletableFuture.completedFuture(null);
        }
    }
}
