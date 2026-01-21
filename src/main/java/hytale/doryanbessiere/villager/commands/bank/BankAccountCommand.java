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
import hytale.doryanbessiere.villager.HytaleVillager;
import hytale.doryanbessiere.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.villager.dto.economy.bank.account.BankTransactionData;
import hytale.doryanbessiere.villager.exceptions.bank.account.BankAccountNameAlreadyExistsException;
import hytale.doryanbessiere.villager.exceptions.bank.account.BankAccountNotFoundException;
import hytale.doryanbessiere.villager.exceptions.bank.BankNotFoundException;
import hytale.doryanbessiere.villager.exceptions.bank.account.transaction.AmountMustBePositiveException;
import hytale.doryanbessiere.villager.exceptions.bank.account.transaction.InsufficientFundsException;
import hytale.doryanbessiere.villager.services.bank.BankService;
import hytale.doryanbessiere.villager.utils.command.NewArgTypes;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
 * # Account commands
 * - /bank account list <bank_name>
 * - /bank account <open|close|details> <bank_name> <accountName>
 * - /bank account transaction deposit <bank_name> <accountName> <amount>
 * - /bank account transaction withdraw <bank_name> <accountName> <amount>
 */
public class BankAccountCommand extends AbstractCommandCollection {

    public BankAccountCommand() {
        super("account", "Create a new bank account");

        this.addUsageVariant(new BankAccountManageCommand());

        this.addSubCommand(new BankAccountListCommand());
        this.addSubCommand(new BankAccountTransactionCommand());
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

    class BankAccountTransactionCommand extends AbstractAsyncPlayerCommand {

        public enum BankAccountTransactionAction {

            DEPOSIT,
            WITHDRAW;

            /**
             * Get the action from a string.
             *
             * @param action
             * @return
             */
            public static BankAccountTransactionAction getAction(String action) {
                for (BankAccountTransactionAction manageAction : BankAccountTransactionAction.values())
                    if (manageAction.name().equalsIgnoreCase(action))
                        return manageAction;
                return null;
            }

            /**
             * Get a comma-separated list of all actions.
             *
             * @return
             */
            public static String actionsList() {
                StringBuilder actions = new StringBuilder();
                for (BankAccountTransactionAction action : BankAccountTransactionAction.values()) {
                    actions.append(action.name().toLowerCase()).append(", ");
                }
                return actions.substring(0, actions.length() - 2);
            }
        }

        private final RequiredArg<String> actionArg = this.withRequiredArg("action", "The action to perform (" + BankAccountTransactionAction.actionsList() + ")", ArgTypes.STRING);
        private final RequiredArg<String> bankNameArg = this.withRequiredArg("bank_name", "The name of the bank", ArgTypes.STRING);
        private final RequiredArg<String> accountNameArg = this.withRequiredArg("accountName", "The name of the bank account", ArgTypes.STRING);
        private final RequiredArg<Long> amountArg = this.withRequiredArg("amount", "The amount to the transaction", NewArgTypes.LONG);

        public BankAccountTransactionCommand() {
            super("transaction", "Manage your bank account transactions");
        }

        @Override
        protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
            CommandSender sender = commandContext.sender();

            String actionStr = this.actionArg.get(commandContext);
            String bankName = this.bankNameArg.get(commandContext);
            String bankAccountName = this.accountNameArg.get(commandContext);
            long amount = this.amountArg.get(commandContext);

            BankAccountTransactionAction action = BankAccountTransactionAction.getAction(actionStr);
            if (action == null) {
                sender.sendMessage(Message.raw("Invalid action '" + actionStr + "'. Valid actions are: " + BankAccountTransactionAction.actionsList() + "."));
                return CompletableFuture.completedFuture(null);
            }

            try {
                switch (action) {
                    case DEPOSIT:
                        BankService.depositToAccount(playerRef, bankName, bankAccountName, amount);
                        sender.sendMessage(Message.raw("You have successfully deposited " + amount + " coins to your bank account '" + bankAccountName + "' in bank '" + bankName + "'."));
                        break;
                    case WITHDRAW:
                        BankService.withdrawFromAccount(playerRef, bankName, bankAccountName, amount);
                        sender.sendMessage(Message.raw("You have successfully withdrawn " + amount + " coins from your bank account '" + bankAccountName + "' in bank '" + bankName + "'."));
                        break;
                    default:
                        break;
                }
            } catch (AmountMustBePositiveException e) {
                sender.sendMessage(Message.raw("The amount '" + amount + "' is invalid."));
            } catch (InsufficientFundsException e) {
                sender.sendMessage(Message.raw("You do not have enough funds in your bank account '" + bankAccountName + "' in bank '" + bankName + "' to withdraw " + amount + " coins."));
            } catch (BankAccountNotFoundException e) {
                sender.sendMessage(Message.raw("You do not have a bank account with the name '" + bankAccountName + "' in bank '" + bankName + "'."));
            } catch (BankNotFoundException e) {
                sender.sendMessage(Message.raw("Bank with the name '" + bankName + "' does not exist."));
            }

            return CompletableFuture.completedFuture(null);
        }
    }

    class BankAccountManageCommand extends AbstractAsyncPlayerCommand {

        public enum BankAccountManageAction {

            CLOSE,
            OPEN,
            DETAILS;

            /**
             * Get the action from a string.
             *
             * @param action
             * @return
             */
            public static BankAccountManageAction getAction(String action) {
                for (BankAccountManageAction manageAction : BankAccountManageAction.values())
                    if (manageAction.name().equalsIgnoreCase(action))
                        return manageAction;
                return null;
            }

            /**
             * Get a comma-separated list of all actions.
             *
             * @return
             */
            public static String actionsList() {
                StringBuilder actions = new StringBuilder();
                for (BankAccountManageAction action : BankAccountManageAction.values()) {
                    actions.append(action.name().toLowerCase()).append(", ");
                }
                return actions.substring(0, actions.length() - 2);
            }
        }

        private final RequiredArg<String> actionArg = this.withRequiredArg("action", "The action to perform (" + BankAccountManageAction.actionsList() + ")", ArgTypes.STRING);
        private final RequiredArg<String> bankNameArg = this.withRequiredArg("bank_name", "The name of the bank", ArgTypes.STRING);
        private final RequiredArg<String> accountNameArg = this.withRequiredArg("accountName", "The name of the bank account", ArgTypes.STRING);

        public BankAccountManageCommand() {
            super("Manage your bank account");
        }

        @Override
        protected @NonNull CompletableFuture<Void> executeAsync(@NonNull CommandContext commandContext, @NonNull Store<EntityStore> store, @NonNull Ref<EntityStore> ref, @NonNull PlayerRef playerRef, @NonNull World world) {
            CommandSender sender = commandContext.sender();

            String actionStr = this.actionArg.get(commandContext);
            String bankName = this.bankNameArg.get(commandContext);
            String bankAccountName = this.accountNameArg.get(commandContext);

            HytaleVillager.logger().atInfo().log("ctionStr: " + actionStr + ", bankName: " + bankName + ", bankAccountName: " + bankAccountName);

            BankAccountManageAction action = BankAccountManageAction.getAction(actionStr);
            if (action == null) {
                sender.sendMessage(Message.raw("Invalid action '" + actionStr + "'. Valid actions are: " + BankAccountManageAction.actionsList() + "."));
                return CompletableFuture.completedFuture(null);
            }

            try {
                switch (action) {
                    case BankAccountManageAction.OPEN:
                        BankService.openAccount(playerRef, bankName, bankAccountName);
                        sender.sendMessage(Message.raw("You have successfully opened a new bank account '" + bankAccountName + "' in bank '" + bankName + "'."));
                        break;
                    case BankAccountManageAction.CLOSE:
                        BankService.closeAccount(playerRef, bankName, bankAccountName);
                        sender.sendMessage(Message.raw("You have successfully closed your bank account '" + bankAccountName + "' in bank '" + bankName + "'."));
                        break;
                    case BankAccountManageAction.DETAILS:
                        BankData bankData = BankService.getBankByName(bankName);
                        BankAccountData bankAccountData = BankService.getBankAccountByName(bankData.getId(), playerRef.getUuid(), bankAccountName);

                        List<BankTransactionData> transactions = bankAccountData.getTransactions();
                        if (bankAccountData == null) {
                            sender.sendMessage(Message.raw("You do not have a bank account with the name '" + bankAccountName + "' in bank '" + bankName + "'."));
                        } else {
                            sender.sendMessage(Message.raw("--------------------------------"));
                            sender.sendMessage(Message.raw("Details of your bank account '" + bankAccountName + "' in bank '" + bankName + "':"));
                            sender.sendMessage(Message.raw(" - Balance: " + bankAccountData.getBalance() + " coins"));
                            sender.sendMessage(Message.raw(" Transactions:"));
                            if (transactions.isEmpty()) {
                                sender.sendMessage(Message.raw("   * No transactions found."));
                                break;
                            } else {
                                for (BankTransactionData transaction : transactions) {
                                    sender.sendMessage(Message.raw("   - " + transaction.getTransactionId() + ": " + transaction.getAmount() + " coins").color(transaction.getAmount() > 0 ? Color.GREEN : Color.RED));
                                }
                            }
                            sender.sendMessage(Message.raw("--------------------------------"));
                        }
                        break;
                    default:
                        break;
                }
            } catch (BankAccountNotFoundException e) {
                sender.sendMessage(Message.raw("You do not have a bank account with the name '" + bankAccountName + "' in bank '" + bankName + "'."));
            } catch (BankAccountNameAlreadyExistsException e) {
                sender.sendMessage(Message.raw("You already have a bank account with the name '" + bankAccountName + "' in bank '" + bankName + "'."));
            } catch (BankNotFoundException e) {
                sender.sendMessage(Message.raw("Bank with the name '" + bankName + "' does not exist."));
            }

            return CompletableFuture.completedFuture(null);
        }
    }
}