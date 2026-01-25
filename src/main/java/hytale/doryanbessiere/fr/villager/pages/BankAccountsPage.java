package hytale.doryanbessiere.fr.villager.pages;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.account.BankTransactionData;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankNotFoundException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.BankAccountNameAlreadyExistsException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.BankAccountNotEmptyException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.BankAccountNotFoundException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.transaction.AmountMustBePositiveException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.transaction.InsufficientFundsException;
import hytale.doryanbessiere.fr.villager.items.BankCheckItem;
import hytale.doryanbessiere.fr.villager.services.bank.BankService;
import hytale.doryanbessiere.fr.utils.PlayerUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;

public class BankAccountsPage extends InteractiveCustomUIPage<BankAccountsPageEventData> {

    private final BankData bankData;
    private String selectedAccountName;

    public BankAccountsPage(@Nonnull PlayerRef playerRef, @Nonnull BankData bankData) {
        super(playerRef, CustomPageLifetime.CanDismiss, BankAccountsPageEventData.CODEC);
        this.bankData = bankData;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder commandBuilder,
                      @Nonnull UIEventBuilder eventBuilder,
                      @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/BankAccountsPage.ui");
        commandBuilder.set("#BankName.Text", bankData.getName());
        commandBuilder.set("#Amount.Value", "0");

        List<BankAccountData> accounts;
        try {
            accounts = BankService.getAccountsById(bankData.getId());
        } catch (BankException e) {
            return;
        }
        int index = 0;
        commandBuilder.clear("#AccountList");
        for (BankAccountData account : accounts) {
            if (!account.getOwnerId().equals(playerRef.getUuid())) {
                continue;
            }
            String selector = "#AccountList[" + index + "]";
            commandBuilder.append("#AccountList", "Pages/BankAccountRow.ui");
            commandBuilder.set(selector + " #Name.Text", account.getAccountName());
            commandBuilder.set(selector + " #Balance.Text", "Balance: " + account.getBalance());
            bindAccountAction(eventBuilder, selector + " #DetailsButton", "DETAILS", account.getAccountName());
            bindAccountAction(eventBuilder, selector + " #DepositButton", "DEPOSIT", account.getAccountName());
            bindAccountAction(eventBuilder, selector + " #WithdrawButton", "WITHDRAW", account.getAccountName());
            bindAccountAction(eventBuilder, selector + " #CloseButton", "CLOSE", account.getAccountName());
            bindAccountAction(eventBuilder, selector + " #CreateCheckButton", "CREATE_CHECK", account.getAccountName());
            bindAccountAction(eventBuilder, selector + " #DepositCheckButton", "DEPOSIT_CHECK", account.getAccountName());
            index++;
        }

        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#OpenAccountButton",
                new EventData()
                        .append(BankAccountsPageEventData.KEY_ACTION, "OPEN")
                        .append(BankAccountsPageEventData.KEY_ACCOUNT_NAME, "#NewAccountName.Value")
                        .append(BankAccountsPageEventData.KEY_AMOUNT, "#Amount.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ManageBanksButton",
                new EventData()
                        .append(BankAccountsPageEventData.KEY_ACTION, "OPEN_MANAGEMENT")
                        .append(BankAccountsPageEventData.KEY_ACCOUNT_NAME, "")
                        .append(BankAccountsPageEventData.KEY_AMOUNT, "0"));

        buildSelectedAccount(commandBuilder);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull BankAccountsPageEventData eventData) {
        String action = safeUpper(eventData.getAction());
        String accountName = safeTrim(eventData.getAccountName());
        String amountRaw = safeTrim(eventData.getAmount());
        long amount = parseAmount(amountRaw);

        switch (action) {
            case "OPEN" -> handleOpenAccount(accountName);
            case "CLOSE" -> handleCloseAccount(accountName);
            case "DETAILS" -> handleDetails(accountName);
            case "DEPOSIT" -> handleDeposit(accountName, amount);
            case "WITHDRAW" -> handleWithdraw(accountName, amount);
            case "CREATE_CHECK" -> handleCreateCheck(accountName, amount);
            case "DEPOSIT_CHECK" -> handleDepositCheck(accountName);
            case "OPEN_MANAGEMENT" -> openBankManagement(ref, store);
            default -> playerRef.sendMessage(Message.raw("Unknown action: " + action));
        }
    }

    private void bindAccountAction(UIEventBuilder eventBuilder, String selector, String action, String accountName) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector,
                new EventData()
                        .append(BankAccountsPageEventData.KEY_ACTION, action)
                        .append(BankAccountsPageEventData.KEY_ACCOUNT_NAME, accountName)
                        .append(BankAccountsPageEventData.KEY_AMOUNT, "#Amount.Value"));
    }

    private void buildSelectedAccount(UICommandBuilder commandBuilder) {
        commandBuilder.clear("#TransactionList");
        if (selectedAccountName == null || selectedAccountName.isEmpty()) {
            commandBuilder.set("#SelectedAccountName.Text", "Select an account to view details.");
            commandBuilder.set("#SelectedBalance.Text", "");
            return;
        }

        BankAccountData bankAccountData;
        try {
            bankAccountData = BankService.getBankAccountByName(
                    bankData.getId(),
                    playerRef.getUuid(),
                    selectedAccountName
            );
        } catch (BankException e) {
            commandBuilder.set("#SelectedAccountName.Text", "Select an account to view details.");
            commandBuilder.set("#SelectedBalance.Text", "");
            return;
        }

        commandBuilder.set("#SelectedAccountName.Text", bankAccountData.getAccountName());
        commandBuilder.set("#SelectedBalance.Text", "Balance: " + bankAccountData.getBalance() + " coins");

        List<BankTransactionData> transactions = new ArrayList<>(bankAccountData.getTransactions());
        transactions.sort(Comparator.comparing(BankTransactionData::getCreatedAt).reversed());
        if (transactions.isEmpty()) {
            commandBuilder.append("#TransactionList", "Pages/BankTransactionRow.ui");
            commandBuilder.set("#TransactionList[0] #TransactionId.Text", "No transactions found.");
            commandBuilder.set("#TransactionList[0] #TransactionAmount.Text", "");
            return;
        }

        int index = 0;
        for (BankTransactionData transaction : transactions) {
            String selector = "#TransactionList[" + index + "]";
            commandBuilder.append("#TransactionList", "Pages/BankTransactionRow.ui");
            commandBuilder.set(selector + " #TransactionId.Text", transaction.getTransactionId());
            commandBuilder.set(selector + " #TransactionAmount.Text", transaction.getAmount() + " coins");
            index++;
        }
    }

    private void handleOpenAccount(String accountName) {
        if (accountName.isEmpty()) {
            playerRef.sendMessage(Message.raw("Account name is required."));
            return;
        }
        try {
            BankService.openAccount(playerRef, bankData.getName(), accountName);
            playerRef.sendMessage(Message.raw("You have successfully opened a new bank account '" + accountName + "' in bank '" + bankData.getName() + "'."));
            rebuild();
        } catch (BankAccountNameAlreadyExistsException e) {
            playerRef.sendMessage(Message.raw("You already have a bank account with the name '" + accountName + "' in bank '" + bankData.getName() + "'."));
        } catch (BankNotFoundException e) {
            playerRef.sendMessage(Message.raw("Bank with the name '" + bankData.getName() + "' does not exist."));
        }
    }

    private void handleCloseAccount(String accountName) {
        if (accountName.isEmpty()) {
            playerRef.sendMessage(Message.raw("Account name is required."));
            return;
        }
        try {
            BankService.closeAccount(playerRef, bankData.getName(), accountName);
            playerRef.sendMessage(Message.raw("You have successfully closed your bank account '" + accountName + "' in bank '" + bankData.getName() + "'."));
            rebuild();
        } catch (BankAccountNotFoundException e) {
            playerRef.sendMessage(Message.raw("You do not have a bank account with the name '" + accountName + "' in bank '" + bankData.getName() + "'."));
        } catch (BankAccountNotEmptyException e) {
            playerRef.sendMessage(Message.raw("Bank account '" + accountName + "' must be empty before closing."));
        } catch (BankNotFoundException e) {
            playerRef.sendMessage(Message.raw("Bank with the name '" + bankData.getName() + "' does not exist."));
        }
    }

    private void handleDetails(String accountName) {
        if (accountName.isEmpty()) {
            playerRef.sendMessage(Message.raw("Account name is required."));
            return;
        }
        selectedAccountName = accountName;
        rebuild();
    }

    private void handleDeposit(String accountName, long amount) {
        if (!validateAmount(amount)) {
            return;
        }
        try {
            BankService.depositToAccount(playerRef, bankData.getName(), accountName, amount);
            playerRef.sendMessage(Message.raw("You have successfully deposited " + amount + " coins to your bank account '" + accountName + "' in bank '" + bankData.getName() + "'."));
            rebuild();
        } catch (AmountMustBePositiveException e) {
            playerRef.sendMessage(Message.raw("The amount '" + amount + "' is invalid."));
        } catch (InsufficientFundsException e) {
            playerRef.sendMessage(Message.raw("You do not have enough funds in your bank account '" + accountName + "' in bank '" + bankData.getName() + "' to deposit " + amount + " coins."));
        } catch (BankAccountNotFoundException e) {
            playerRef.sendMessage(Message.raw("You do not have a bank account with the name '" + accountName + "' in bank '" + bankData.getName() + "'."));
        } catch (BankNotFoundException e) {
            playerRef.sendMessage(Message.raw("Bank with the name '" + bankData.getName() + "' does not exist."));
        }
    }

    private void handleWithdraw(String accountName, long amount) {
        if (!validateAmount(amount)) {
            return;
        }
        try {
            BankService.withdrawFromAccount(playerRef, bankData.getName(), accountName, amount);
            playerRef.sendMessage(Message.raw("You have successfully withdrawn " + amount + " coins from your bank account '" + accountName + "' in bank '" + bankData.getName() + "'."));
            rebuild();
        } catch (AmountMustBePositiveException e) {
            playerRef.sendMessage(Message.raw("The amount '" + amount + "' is invalid."));
        } catch (InsufficientFundsException e) {
            playerRef.sendMessage(Message.raw("You do not have enough funds in your bank account '" + accountName + "' in bank '" + bankData.getName() + "' to withdraw " + amount + " coins."));
        } catch (BankAccountNotFoundException e) {
            playerRef.sendMessage(Message.raw("You do not have a bank account with the name '" + accountName + "' in bank '" + bankData.getName() + "'."));
        } catch (BankNotFoundException e) {
            playerRef.sendMessage(Message.raw("Bank with the name '" + bankData.getName() + "' does not exist."));
        }
    }

    private void handleCreateCheck(String accountName, long amount) {
        if (!validateAmount(amount)) {
            return;
        }
        try {
            BankService.createBankCheck(playerRef, bankData.getName(), accountName, amount);
            playerRef.sendMessage(Message.raw("You have successfully created a bank check of " + amount + " coins from your bank account '" + accountName + "' in bank '" + bankData.getName() + "'."));
            rebuild();
        } catch (AmountMustBePositiveException e) {
            playerRef.sendMessage(Message.raw("The amount '" + amount + "' is invalid."));
        } catch (InsufficientFundsException e) {
            playerRef.sendMessage(Message.raw("You do not have enough funds in your bank account '" + accountName + "' in bank '" + bankData.getName() + "' to create a bank check of " + amount + " coins."));
        } catch (BankAccountNotFoundException e) {
            playerRef.sendMessage(Message.raw("You do not have a bank account with the name '" + accountName + "' in bank '" + bankData.getName() + "'."));
        } catch (BankNotFoundException e) {
            playerRef.sendMessage(Message.raw("Bank with the name '" + bankData.getName() + "' does not exist."));
        }
    }

    private void handleDepositCheck(String accountName) {
        var player = PlayerUtils.getPlayer(playerRef);
        if (player == null || player.getInventory() == null) {
            playerRef.sendMessage(Message.raw("Unable to access your inventory."));
            return;
        }
        var itemInHand = player.getInventory().getItemInHand();
        if (!BankCheckItem.isBankCheck(itemInHand)) {
            playerRef.sendMessage(Message.raw("You must hold a valid bank check in your hand to deposit it."));
            return;
        }
        try {
            BankService.depositBankCheck(playerRef, bankData.getName(), accountName, itemInHand);
            playerRef.sendMessage(Message.raw("You have successfully deposited the bank check in your hand to your bank account '" + accountName + "' in bank '" + bankData.getName() + "'."));
            rebuild();
        } catch (AmountMustBePositiveException e) {
            playerRef.sendMessage(Message.raw("The amount '" + e.getAmount() + "' is invalid."));
        } catch (BankAccountNotFoundException e) {
            playerRef.sendMessage(Message.raw("You do not have a bank account with the name '" + accountName + "' in bank '" + bankData.getName() + "'."));
        } catch (BankNotFoundException e) {
            playerRef.sendMessage(Message.raw("Bank with the name '" + bankData.getName() + "' does not exist."));
        }
    }

    private void openBankManagement(Ref<EntityStore> ref, Store<EntityStore> store) {
        var player = PlayerUtils.getPlayer(playerRef);
        if (player == null) {
            playerRef.sendMessage(Message.raw("Unable to open bank management right now."));
            return;
        }
        player.getPageManager().openCustomPage(ref, store, new BankManagementPage(playerRef));
    }

    private long parseAmount(String amountRaw) {
        if (amountRaw == null || amountRaw.isBlank()) {
            return 0L;
        }
        try {
            return Long.parseLong(amountRaw.trim());
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    private boolean validateAmount(long amount) {
        if (amount <= 0) {
            playerRef.sendMessage(Message.raw("The amount '" + amount + "' is invalid."));
            return false;
        }
        return true;
    }

    private String safeUpper(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
