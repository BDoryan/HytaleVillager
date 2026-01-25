package hytale.doryanbessiere.fr.villager.pages;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankType;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankNameAlreadyExistsException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankNotFoundException;
import hytale.doryanbessiere.fr.villager.services.bank.BankService;
import hytale.doryanbessiere.fr.utils.PlayerUtils;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;

public class BankManagementPage extends InteractiveCustomUIPage<BankManagementPageEventData> {

    private String selectedBankName;

    public BankManagementPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, BankManagementPageEventData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref,
                      @Nonnull UICommandBuilder commandBuilder,
                      @Nonnull UIEventBuilder eventBuilder,
                      @Nonnull Store<EntityStore> store) {
        commandBuilder.append("Pages/BankManagementPage.ui");
        commandBuilder.set("#CreateType.Value", "VILLAGE");

        List<BankData> banks = BankService.getBanks();
        commandBuilder.clear("#BankList");
        int index = 0;
        for (BankData bank : banks) {
            String selector = "#BankList[" + index + "]";
            commandBuilder.append("#BankList", "Pages/BankRow.ui");
            commandBuilder.set(selector + " #Name.Text", bank.getName());
            commandBuilder.set(selector + " #Type.Text", bank.getType().name());
            commandBuilder.set(selector + " #Accounts.Text", "Accounts: " + bank.getTotalAccounts());
            commandBuilder.set(selector + " #Balance.Text", "Balance: " + bank.getTotalBalance());
            bindBankAction(eventBuilder, selector + " #InfoButton", "INFO", bank.getName(), bank.getType().name());
            bindBankAction(eventBuilder, selector + " #OpenButton", "OPEN_ACCOUNTS", bank.getName(), bank.getType().name());
            bindBankAction(eventBuilder, selector + " #DeleteButton", "DELETE", bank.getName(), bank.getType().name());
            index++;
        }

        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CreateButton",
                new EventData()
                        .append(BankManagementPageEventData.KEY_ACTION, "CREATE")
                        .append(BankManagementPageEventData.KEY_BANK_NAME, "#CreateName.Value")
                        .append(BankManagementPageEventData.KEY_BANK_TYPE, "#CreateType.Value"));
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#RefreshButton",
                new EventData()
                        .append(BankManagementPageEventData.KEY_ACTION, "REFRESH")
                        .append(BankManagementPageEventData.KEY_BANK_NAME, "")
                        .append(BankManagementPageEventData.KEY_BANK_TYPE, ""));

        buildSelectedBank(commandBuilder);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref,
                                @Nonnull Store<EntityStore> store,
                                @Nonnull BankManagementPageEventData eventData) {
        String action = safeUpper(eventData.getAction());
        String bankName = safeTrim(eventData.getBankName());
        String bankTypeRaw = safeTrim(eventData.getBankType());

        switch (action) {
            case "CREATE" -> handleCreate(bankName, bankTypeRaw);
            case "DELETE" -> handleDelete(bankName);
            case "INFO" -> handleInfo(bankName);
            case "OPEN_ACCOUNTS" -> handleOpenAccounts(ref, store, bankName);
            case "REFRESH" -> rebuild();
            default -> playerRef.sendMessage(Message.raw("Unknown action: " + action));
        }
    }

    private void bindBankAction(UIEventBuilder eventBuilder, String selector, String action, String bankName, String bankType) {
        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating, selector,
                new EventData()
                        .append(BankManagementPageEventData.KEY_ACTION, action)
                        .append(BankManagementPageEventData.KEY_BANK_NAME, bankName)
                        .append(BankManagementPageEventData.KEY_BANK_TYPE, bankType));
    }

    private void buildSelectedBank(UICommandBuilder commandBuilder) {
        if (selectedBankName == null || selectedBankName.isEmpty()) {
            commandBuilder.set("#SelectedBankName.Text", "Select a bank to view details.");
            commandBuilder.set("#SelectedBankType.Text", "");
            commandBuilder.set("#SelectedBankAccounts.Text", "");
            commandBuilder.set("#SelectedBankBalance.Text", "");
            return;
        }

        try {
            BankData bankData = BankService.getBankByName(selectedBankName);
            commandBuilder.set("#SelectedBankName.Text", bankData.getName());
            commandBuilder.set("#SelectedBankType.Text", "Type: " + bankData.getType().name());
            commandBuilder.set("#SelectedBankAccounts.Text", "Accounts: " + bankData.getTotalAccounts());
            commandBuilder.set("#SelectedBankBalance.Text", "Balance: " + bankData.getTotalBalance());
        } catch (BankNotFoundException e) {
            commandBuilder.set("#SelectedBankName.Text", "Select a bank to view details.");
            commandBuilder.set("#SelectedBankType.Text", "");
            commandBuilder.set("#SelectedBankAccounts.Text", "");
            commandBuilder.set("#SelectedBankBalance.Text", "");
        }
    }

    private void handleCreate(String bankName, String bankTypeRaw) {
        if (bankName.isEmpty()) {
            playerRef.sendMessage(Message.raw("Bank name is required."));
            return;
        }
        BankType type;
        try {
            type = BankType.valueOf(bankTypeRaw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            playerRef.sendMessage(Message.raw("Invalid bank type. Use VILLAGE or GOVERNMENT."));
            return;
        }

        try {
            BankService.createBank(bankName, type, playerRef);
            playerRef.sendMessage(Message.raw("Bank '" + bankName + "' of type '" + type + "' created successfully."));
            rebuild();
        } catch (BankNameAlreadyExistsException e) {
            playerRef.sendMessage(Message.raw("A bank with the name '" + bankName + "' already exists."));
        }
    }

    private void handleDelete(String bankName) {
        if (bankName.isEmpty()) {
            playerRef.sendMessage(Message.raw("Bank name is required."));
            return;
        }
        try {
            BankService.deleteBank(bankName);
            playerRef.sendMessage(Message.raw("Bank with the name '" + bankName + "' has been deleted successfully."));
            if (bankName.equalsIgnoreCase(selectedBankName)) {
                selectedBankName = null;
            }
            rebuild();
        } catch (BankNotFoundException e) {
            playerRef.sendMessage(Message.raw("Bank with the name '" + bankName + "' does not exist."));
        }
    }

    private void handleInfo(String bankName) {
        if (bankName.isEmpty()) {
            playerRef.sendMessage(Message.raw("Bank name is required."));
            return;
        }
        selectedBankName = bankName;
        rebuild();
    }

    private void handleOpenAccounts(Ref<EntityStore> ref, Store<EntityStore> store, String bankName) {
        if (bankName.isEmpty()) {
            playerRef.sendMessage(Message.raw("Bank name is required."));
            return;
        }
        BankData bankData;
        try {
            bankData = BankService.getBankByName(bankName);
        } catch (BankNotFoundException e) {
            playerRef.sendMessage(Message.raw("Bank with the name '" + bankName + "' does not exist."));
            return;
        }

        var player = PlayerUtils.getPlayer(playerRef);
        if (player == null) {
            playerRef.sendMessage(Message.raw("Unable to open bank accounts right now."));
            return;
        }
        player.getPageManager().openCustomPage(ref, store, new BankAccountsPage(playerRef, bankData));
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
