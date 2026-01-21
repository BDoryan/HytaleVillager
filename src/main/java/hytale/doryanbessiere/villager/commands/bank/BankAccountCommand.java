package hytale.doryanbessiere.villager.commands.bank;


import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import hytale.doryanbessiere.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.villager.exceptions.BankAccountNameIsAlreadyExistException;
import hytale.doryanbessiere.villager.exceptions.BankNotFoundException;
import hytale.doryanbessiere.villager.services.bank.BankService;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
 * # Account commands
 * - /bank account list <bank_name>
 * - /bank account details <bank_name> <accountName>
 * - /bank account open <bank_name> <accountName>
 * - /bank account close <bank_name> <accountName>
 * - /bank account deposit <bank_name> <accountName> <amount>
 * - /bank account withdraw <bank_name> <accountName> <amount>
 */
public class BankAccountCommand extends AbstractCommandCollection {

    public BankAccountCommand() {
        super("account", "Create a new bank account");

        this.addSubCommand(new BankAccountListCommand());
        this.addSubCommand(new BankAccountDetailsCommand());
        this.addSubCommand(new BankAccountOpenCommand());
    }

    class BankAccountDetailsCommand extends AbstractAsyncPlayerCommand {

        private final RequiredArg<String> bankNameArg = this.withRequiredArg("bank_name", "The name of the bank", ArgTypes.STRING);
        private final RequiredArg<String> accountNameArg = this.withRequiredArg("accountName", "The name of the bank account", ArgTypes.STRING);

        public BankAccountDetailsCommand() {
            super("details", "Get details of a bank account");
        }

        @Override
        protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
            CommandSender sender = commandContext.sender();

            String bankName = this.bankNameArg.get(commandContext);
            String bankAccountName = this.accountNameArg.get(commandContext);

            return CompletableFuture.completedFuture(null);
        }
    }

    class BankAccountListCommand extends AbstractAsyncPlayerCommand {

        private final RequiredArg<String> bankNameArg = this.withRequiredArg("bank_name", "The name of the bank", ArgTypes.STRING);

        public BankAccountListCommand() {
            super("list", "List your bank accounts in a bank");
        }

        @Override
        protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
            CommandSender sender = commandContext.sender();

            String bankName = this.bankNameArg.get(commandContext);
            try {
                BankData bankData = BankService.getBankByName(bankName);
                List<BankAccountData> bankAccountData = BankService.getAccountsById(bankData.getId());

                sender.sendMessage(Message.raw("Your bank accounts in bank '"));
                for (BankAccountData account : bankAccountData) {
                    if (account.getOwnerId().equals(playerRef.getUuid())) {
                        sender.sendMessage(Message.raw(" - " + account.getAccountName() + ": " + account.getBalance() + " coins"));
                    }
                }
                sender.sendMessage(Message.raw(""));
            } catch (BankNotFoundException e) {
                sender.sendMessage(Message.raw("Bank with the name '" + bankName + "' does not exist."));
            }

            return CompletableFuture.completedFuture(null);
        }
    }

    class BankAccountOpenCommand extends AbstractAsyncPlayerCommand {

        private final RequiredArg<String> bankNameArg = this.withRequiredArg("bank_name", "The name of the bank", ArgTypes.STRING);
        private final RequiredArg<String> accountNameArg = this.withRequiredArg("accountName", "The name of the bank account", ArgTypes.STRING);

        public BankAccountOpenCommand() {
            super("open", "Open a new bank account");
        }

        @Override
        protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
            CommandSender sender = commandContext.sender();

            String bankName = this.bankNameArg.get(commandContext);
            String bankAccountName = this.accountNameArg.get(commandContext);
            try {
                BankService.openAccount(playerRef, bankName, bankAccountName);
                sender.sendMessage(Message.raw("You have successfully opened a new bank account '" + bankAccountName + "' in bank '" + bankName + "'."));
            } catch (BankAccountNameIsAlreadyExistException e) {
                sender.sendMessage(Message.raw("You already have a bank account with the name '" + bankAccountName + "' in bank '" + bankName + "'."));
            } catch (BankNotFoundException e) {
                sender.sendMessage(Message.raw("Bank with the name '" + bankName + "' does not exist."));
            }
            return CompletableFuture.completedFuture(null);
        }
    }
}