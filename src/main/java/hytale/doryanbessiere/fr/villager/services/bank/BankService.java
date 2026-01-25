package hytale.doryanbessiere.fr.villager.services.bank;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.interaction.Interactions;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import hytale.doryanbessiere.fr.villager.components.BankLinkComponent;
import hytale.doryanbessiere.fr.villager.dto.PlayerData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankType;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.account.BankTransactionData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.bankcheck.BankCheckData;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankNameAlreadyExistsException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankNotFoundException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.BankAccountNameAlreadyExistsException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.BankAccountNotEmptyException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.BankAccountNotFoundException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.transaction.InsufficientFundsException;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.transaction.AmountMustBePositiveException;
import hytale.doryanbessiere.fr.villager.exceptions.player.PlayerNotFoundException;
import hytale.doryanbessiere.fr.villager.items.BankCheckItem;
import hytale.doryanbessiere.fr.villager.repository.adapter.file.bank.BankRepositoryFile;
import hytale.doryanbessiere.fr.villager.repository.bank.BankAccountRepository;
import hytale.doryanbessiere.fr.villager.repository.bank.BankRepository;
import hytale.doryanbessiere.fr.villager.services.PlayerService;
import hytale.doryanbessiere.fr.utils.PlayerUtils;
import hytale.doryanbessiere.fr.utils.entity.NpcBuilder;
import hytale.doryanbessiere.fr.utils.entity.NpcUtils;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

public class BankService {

    private static final HytaleLogger logger = HytaleLogger.forEnclosingClass();
    private static final BankRepository bankRepository = new BankRepositoryFile();

    /**
     * Create a new bank
     *
     * @param name      bank name
     * @param type      bank type
     * @param playerRef creator
     * @throws BankNameAlreadyExistsException if a bank with the same name already exists
     */
    public static void createBank(String name, BankType type, PlayerRef playerRef) throws BankException {
        BankData bankData = new BankData(name, type);
        if (bankRepository.findByName(bankData.getName()) != null)
            throw new BankNameAlreadyExistsException(bankData.getName());

        Player player = PlayerUtils.getPlayer(playerRef);
        World world = player.getWorld();
        NpcBuilder builder = NpcBuilder.create("Klops_Merchant", world, playerRef.getTransform().getPosition())
                .interactive(InteractionType.Use, "Root_Bank_Open")
                .persistent(true)
                .roleName("LookAtMe")
                .displayName(bankData.getName())
                .build();

        builder.getHolder().addComponent(BankLinkComponent.getComponentType(), new BankLinkComponent(bankData.getId()));
        UUID uuid = builder.spawn();

        bankData.setEntityId(uuid);

        bankRepository.create(bankData);
        logger.atInfo().log("Bank '" + name + "' created by player '" + playerRef.getUsername() + "'");
    }

    /**
     * Get all bank accounts by bank id
     *
     * @param bankId bank id
     * @return all accounts of this bank
     * @throws BankNotFoundException if the bank does not exist
     */
    public static List<BankAccountData> getAccountsById(UUID bankId) throws BankException {
        BankData bankData = getBank(bankId);
        if (bankData == null)
            throw new BankNotFoundException(bankId.toString());

        BankAccountRepository bankAccountRepository = bankRepository.getBankAccountRepository(bankData.getId());
        return bankAccountRepository.findAll();
    }

    /**
     * Open a bank account at a bank by name for a player
     *
     * @param playerRef   player
     * @param bankName    bank name
     * @param accountName account name
     * @return bank data
     * @throws BankNotFoundException                 if the bank does not exist
     * @throws BankAccountNameAlreadyExistsException if the player already has an account with this name in this bank
     */
    public static BankData openAccount(PlayerRef playerRef, String bankName, String accountName) throws BankException {
        BankData bankData = getBankByName(bankName);

        BankAccountRepository bankAccountRepository = bankRepository.getBankAccountRepository(bankData.getId());
        BankAccountData existing = bankAccountRepository.findAccountByName(playerRef.getUuid(), accountName);
        if (existing != null)
            throw new BankAccountNameAlreadyExistsException(accountName);

        BankAccountData bankAccountData = new BankAccountData(playerRef, accountName);
        bankAccountRepository.create(bankAccountData);

        logger.atInfo().log("Bank account '" + bankAccountData.getAccountName() + "' opened at bank '" + bankData.getName() + "' by player '" + playerRef.getUsername() + "'");

        return bankData;
    }

    /**
     * Close a bank account at a bank by name for a player
     *
     * @param playerRef       player
     * @param bankName        bank name
     * @param bankAccountName bank account name
     * @throws BankNotFoundException        if the bank does not exist
     * @throws BankAccountNotFoundException if the account does not exist
     * @throws BankAccountNotEmptyException if the account balance is not 0
     */
    public static void closeAccount(@NonNull PlayerRef playerRef, String bankName, String bankAccountName) throws BankException {
        BankData bankData = getBankByName(bankName);
        BankAccountData bankAccountData = getBankAccountByName(bankData.getId(), playerRef.getUuid(), bankAccountName);

        if (bankAccountData.getBalance() != 0)
            throw new BankAccountNotEmptyException(bankAccountData);

        BankAccountRepository bankAccountRepository = bankRepository.getBankAccountRepository(bankData.getId());
        bankAccountRepository.delete(bankAccountData);

        logger.atInfo().log("Bank account '" + bankAccountData.getAccountName() + "' closed at bank '" + bankData.getName() + "' by player '" + playerRef.getUsername() + "'");
    }

    /**
     * Deposit money from player's wallet to a bank account.
     *
     * @param playerRef       player
     * @param bankName        bank name
     * @param bankAccountName bank account name
     * @param amount          amount to deposit
     * @throws BankNotFoundException         if the bank does not exist
     * @throws BankAccountNotFoundException  if the bank account does not exist
     * @throws AmountMustBePositiveException if amount must be positive
     * @throws InsufficientFundsException    if the player balance is insufficient
     * @throws PlayerNotFoundException       if the player does not exist
     */
    public static void depositToAccount(@NonNull PlayerRef playerRef, String bankName, String bankAccountName, long amount) throws BankException {
        BankData bankData = getBankByName(bankName);
        BankAccountData bankAccountData = getBankAccountByName(bankData.getId(), playerRef.getUuid(), bankAccountName);
        PlayerData playerData = PlayerService.getPlayerData(playerRef);

        if (amount <= 0)
            throw new AmountMustBePositiveException(amount);

        long currentPlayerBalance = playerData.getBalance();
        if (amount > currentPlayerBalance)
            throw new InsufficientFundsException(currentPlayerBalance, amount);

        BankTransactionData transactionData = new BankTransactionData(BankTransactionData.TransactionType.DEPOSIT, bankAccountData.getId(), amount);
        bankAccountData.addTransaction(transactionData);
        playerData.setBalance(currentPlayerBalance - amount);

        BankService.saveBankAccount(bankData, bankAccountData);
        PlayerService.savePlayerData(playerData);

        logger.atInfo().log("Deposited " + amount + " to bank account '" + bankAccountData.getAccountName()
                + "' at bank '" + bankData.getName() + "' by player '" + playerRef.getUsername() + "'");
    }

    /**
     * Withdraw money from a bank account to player's wallet.
     *
     * @param playerRef       player
     * @param bankName        bank name
     * @param bankAccountName bank account name
     * @param amount          amount to withdraw
     * @throws BankNotFoundException         if the bank does not exist
     * @throws BankAccountNotFoundException  if the bank account does not exist
     * @throws AmountMustBePositiveException if amount must be positive
     * @throws InsufficientFundsException    if the bank account balance is insufficient
     * @throws PlayerNotFoundException       if the player does not exist
     */
    public static void withdrawFromAccount(@NonNull PlayerRef playerRef, String bankName, String bankAccountName, long amount) {
        BankData bankData = getBankByName(bankName);
        BankAccountData bankAccountData = getBankAccountByName(bankData.getId(), playerRef.getUuid(), bankAccountName);
        PlayerData playerData = PlayerService.getPlayerData(playerRef);

        if (amount <= 0)
            throw new AmountMustBePositiveException(amount);

        BankTransactionData transactionData = new BankTransactionData(BankTransactionData.TransactionType.WITHDRAWAL, bankAccountData.getId(), -amount);
        bankAccountData.addTransaction(transactionData);
        playerData.setBalance(playerData.getBalance() + amount);

        BankService.saveBankAccount(bankData, bankAccountData);
        PlayerService.savePlayerData(playerData);

        logger.atInfo().log("Withdrew " + amount + " from bank account '" + bankAccountData.getAccountName()
                + "' at bank '" + bankData.getName() + "' by player '" + playerRef.getUsername() + "'");
    }

    public static void createBankCheck(@NonNull PlayerRef playerRef, String bankName, String bankAccountName, long amount) {
        BankData bankData = getBankByName(bankName);
        BankAccountData bankAccountData = getBankAccountByName(bankData.getId(), playerRef.getUuid(), bankAccountName);

        if (amount <= 0)
            throw new AmountMustBePositiveException(amount);

        if (bankAccountData.getBalance() < amount)
            throw new InsufficientFundsException(bankAccountData.getBalance(), amount);

        // Get player
        Player player = PlayerUtils.getPlayer(playerRef);

        // Give bank check item to player
        ItemStack bankCheckItem = BankCheckItem.createBankCheck(bankAccountData, amount);

        // Add the bank check to the player's inventory (hotbar first) (1/2)
        player.getInventory().getCombinedHotbarFirst().addItemStack(bankCheckItem);

        // Deduct amount from bank account (2/2)
        BankTransactionData transactionData = new BankTransactionData(
                BankTransactionData.TransactionType.BANK_CHECK_CREATE,
                bankAccountData.getId(),
                -amount
        );
        bankAccountData.addTransaction(transactionData);

        // Save bank account
        BankService.saveBankAccount(bankData, bankAccountData);
    }

    public static void depositBankCheck(@NonNull PlayerRef playerRef, String bankName, String bankAccountName, ItemStack bankCheckItem) {
        BankData bankData = getBankByName(bankName);
        BankAccountData bankAccountData = getBankAccountByName(bankData.getId(), playerRef.getUuid(), bankAccountName);
        Player player = PlayerUtils.getPlayer(playerRef);
        BankCheckData bankCheckData = BankCheckItem.getBankCheck(bankCheckItem);

        if (bankCheckData.getAmount() <= 0)
            throw new AmountMustBePositiveException(bankCheckData.getAmount());

        // Create transaction to add amount to the bank account
        BankTransactionData transactionData = bankCheckData.toTransaction();

        // Add transaction to bank account
        bankAccountData.addTransaction(transactionData);

        // Save bank account
        BankService.saveBankAccount(bankData, bankAccountData);

        // Remove the bank check from the player's hand
        player.getInventory().getCombinedHotbarFirst().removeItemStack(bankCheckItem);
    }

    public static void debugBankChecks(@NonNull PlayerRef playerRef) {
        Player player = PlayerUtils.getPlayer(playerRef);
        ItemStack itemInHand = player.getInventory().getItemInHand();
        if (itemInHand != null &&
                itemInHand.getItemId() == "BankCheck" &&
                itemInHand.getMetadata() != null) {
            playerRef.sendMessage(Message.raw("Bank Account ID: " + itemInHand.getMetadata().getString("bankAccountId")));
            playerRef.sendMessage(Message.raw("Amount: " + itemInHand.getMetadata().getString("amount")));
        } else {
            playerRef.sendMessage(Message.raw("No valid bank check in hand."));
            // Debug info
            if (itemInHand == null) {
                playerRef.sendMessage(Message.raw("Item in hand is null."));
            } else {
                playerRef.sendMessage(Message.raw("Item ID: " + itemInHand.getItemId()));
                if (itemInHand.getMetadata() == null) {
                    playerRef.sendMessage(Message.raw("Item metadata is null."));
                } else {
                    playerRef.sendMessage(Message.raw("Item has metadata."));
                }
            }
        }
    }

    /**
     * Get a bank account by name for a player at a bank
     *
     * @param bankId      bank id
     * @param ownerId     owner id
     * @param accountName account name
     * @return bank account data
     * @throws BankNotFoundException        if the bank does not exist
     * @throws BankAccountNotFoundException if the account does not exist
     */
    public static BankAccountData getBankAccountByName(UUID bankId, UUID ownerId, String accountName) throws BankException {
        BankData bankData = getBank(bankId);
        if (bankData == null)
            throw new BankNotFoundException(bankId.toString());

        BankAccountRepository bankAccountRepository = bankRepository.getBankAccountRepository(bankData.getId());
        BankAccountData bankAccountData = bankAccountRepository.findAccountByName(ownerId, accountName);

        if (bankAccountData == null)
            throw new BankAccountNotFoundException(accountName);

        return bankAccountData;
    }

    public static void saveBankAccount(BankData bankData, BankAccountData bankAccountData) {
        BankAccountRepository bankAccountRepository = bankRepository.getBankAccountRepository(bankData.getId());
        bankAccountRepository.set(bankAccountData);
    }

    /**
     * Get a bank by name
     *
     * @param bankName bank name
     * @return bank data
     * @throws BankNotFoundException if the bank does not exist
     */
    public static BankData getBankByName(String bankName) throws BankException {
        BankData bankData = bankRepository.findByName(bankName);
        if (bankData == null)
            throw new BankNotFoundException(bankName);
        return bankData;
    }

    /**
     * Get a bank by id
     *
     * @param bankId bank id
     * @return bank data or null
     */
    public static BankData getBank(UUID bankId) {
        return bankRepository.findById(bankId);
    }

    /**
     * Get all banks
     *
     * @return all banks
     */
    public static List<BankData> getBanks() {
        return bankRepository.findAll();
    }

    public static void ensureBankNpcInteractions() {
        for (BankData bankData : bankRepository.findAll()) {
            UUID entityId = bankData.getEntityId();
            if (entityId == null) {
                continue;
            }

            for (World world : Universe.get().getWorlds().values()) {
                String bankName = bankData.getName();
                world.execute(() -> {
                    Ref<EntityStore> entityRef = world.getEntityRef(entityId);
                    if (entityRef == null || !entityRef.isValid()) {
                        return;
                    }

                    Store<EntityStore> store = world.getEntityStore().getStore();
                    Interactions interactions = store.getComponent(entityRef, Interactions.getComponentType());
                    if (interactions == null) {
                        interactions = new Interactions();
                    }
                    interactions.setInteractionId(InteractionType.Use, "Root_Bank_Open");
                    store.putComponent(entityRef, Interactions.getComponentType(), interactions);

                    if (store.getComponent(entityRef, Interactable.getComponentType()) == null) {
                        store.putComponent(entityRef, Interactable.getComponentType(), Interactable.INSTANCE);
                    }

                    logger.atInfo().log("Reapplied interaction for bank NPC: " + bankName);
                });
            }
        }
    }

    public static void checkAndRespawnInvalidNpcs() {
        for (BankData bankData : bankRepository.findAll()) {
            UUID entityId = bankData.getEntityId();
            if (entityId == null) {
                continue;
            }

            for (World world : Universe.get().getWorlds().values()) {
                String bankName = bankData.getName();
                world.execute(() -> {
                    Ref<EntityStore> entityRef = world.getEntityRef(entityId);
                    if (entityRef == null || !entityRef.isValid()) {
                        return;
                    }

                    Store<EntityStore> store = world.getEntityStore().getStore();
                    boolean updated = false;

                    Interactions interactions = store.getComponent(entityRef, Interactions.getComponentType());
                    if (interactions == null) {
                        interactions = new Interactions();
                        store.putComponent(entityRef, Interactions.getComponentType(), interactions);
                        updated = true;
                    }
                    interactions.setInteractionId(InteractionType.Use, "Root_Bank_Open");

                    if (store.getComponent(entityRef, Interactable.getComponentType()) == null) {
                        store.putComponent(entityRef, Interactable.getComponentType(), Interactable.INSTANCE);
                        updated = true;
                    }

                    if (updated) {
                        logger.atInfo().log("Repaired interaction components for bank NPC: " + bankName);
                    }
                });
            }
        }
    }

    /**
     * Return all transactions from all bank accounts
     *
     * @return
     */
    public static List<BankTransactionData> getAllTransactions() {
        return getAllBankAccounts().stream()
                .flatMap(bankAccountData -> bankAccountData.getTransactions().stream())
                .toList();
    }

    /**
     * Return all bank accounts from all banks
     *
     * @return
     */
    public static List<BankAccountData> getAllBankAccounts() {
        return bankRepository.findAll().stream()
                .flatMap(bankData -> bankRepository.getBankAccountRepository(bankData.getId()).findAll().stream())
                .toList();
    }

    public static void deleteBank(String bankName) {
        BankData bankData = bankRepository.findByName(bankName);
        if (bankData == null)
            throw new BankNotFoundException(bankName);

        // Delete bank NPC entity
        NpcUtils.deleteEntityById(bankData.getEntityId());

        // Delete bank data
        bankRepository.delete(bankData);

        logger.atInfo().log("Bank '" + bankName + "' has been deleted.");
    }
}
