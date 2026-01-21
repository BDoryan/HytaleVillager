package hytale.doryanbessiere.villager.services.bank;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import hytale.doryanbessiere.villager.dto.PlayerData;
import hytale.doryanbessiere.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.villager.dto.economy.bank.BankType;
import hytale.doryanbessiere.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.villager.dto.economy.bank.account.BankTransactionData;
import hytale.doryanbessiere.villager.exceptions.bank.BankException;
import hytale.doryanbessiere.villager.exceptions.bank.BankNameAlreadyExistsException;
import hytale.doryanbessiere.villager.exceptions.bank.BankNotFoundException;
import hytale.doryanbessiere.villager.exceptions.bank.account.BankAccountNameAlreadyExistsException;
import hytale.doryanbessiere.villager.exceptions.bank.account.BankAccountNotEmptyException;
import hytale.doryanbessiere.villager.exceptions.bank.account.BankAccountNotFoundException;
import hytale.doryanbessiere.villager.exceptions.bank.account.transaction.InsufficientFundsException;
import hytale.doryanbessiere.villager.exceptions.bank.account.transaction.AmountMustBePositiveException;
import hytale.doryanbessiere.villager.exceptions.player.PlayerNotFoundException;
import hytale.doryanbessiere.villager.repository.adapter.file.bank.BankRepositoryFile;
import hytale.doryanbessiere.villager.repository.bank.BankAccountRepository;
import hytale.doryanbessiere.villager.repository.bank.BankRepository;
import hytale.doryanbessiere.villager.services.PlayerService;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

public class BankService {

    private static final HytaleLogger logger = HytaleLogger.forEnclosingClass();
    private static final BankRepository bankRepository = new BankRepositoryFile();

    /**
     * Create a new bank
     *
     * @param name bank name
     * @param type bank type
     * @param playerRef creator
     *
     * @throws BankNameAlreadyExistsException if a bank with the same name already exists
     */
    public static void createBank(String name, BankType type, PlayerRef playerRef) throws BankException {
        BankData bankData = new BankData(name, type);
        if (bankRepository.findByName(bankData.getName()) != null)
            throw new BankNameAlreadyExistsException(bankData.getName());

        bankData.applyPositionAndOrientation(playerRef);
        bankRepository.create(bankData);

        logger.atInfo().log("Bank '" + name + "' created by player '" + playerRef.getUsername() + "'");
    }

    /**
     * Get all bank accounts by bank id
     *
     * @param bankId bank id
     * @return all accounts of this bank
     *
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
     * @param playerRef player
     * @param bankName bank name
     * @param accountName account name
     * @return bank data
     *
     * @throws BankNotFoundException if the bank does not exist
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
     * @param playerRef player
     * @param bankName bank name
     * @param bankAccountName bank account name
     *
     * @throws BankNotFoundException if the bank does not exist
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
     * @param playerRef player
     * @param bankName bank name
     * @param bankAccountName bank account name
     * @param amount amount to deposit
     *
     * @throws BankNotFoundException if the bank does not exist
     * @throws BankAccountNotFoundException if the bank account does not exist
     * @throws AmountMustBePositiveException if amount must be positive
     * @throws InsufficientFundsException if the player balance is insufficient
     * @throws PlayerNotFoundException if the player does not exist
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

        BankTransactionData transactionData = new BankTransactionData(bankAccountData.getId(), amount);
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
     * @param playerRef player
     * @param bankName bank name
     * @param bankAccountName bank account name
     * @param amount amount to withdraw
     *
     * @throws BankNotFoundException if the bank does not exist
     * @throws BankAccountNotFoundException if the bank account does not exist
     * @throws AmountMustBePositiveException if amount must be positive
     * @throws InsufficientFundsException if the bank account balance is insufficient
     * @throws PlayerNotFoundException if the player does not exist
     */
    public static void withdrawFromAccount(@NonNull PlayerRef playerRef, String bankName, String bankAccountName, long amount) {
        BankData bankData = getBankByName(bankName);
        BankAccountData bankAccountData = getBankAccountByName(bankData.getId(), playerRef.getUuid(), bankAccountName);
        PlayerData playerData = PlayerService.getPlayerData(playerRef);

        if (amount <= 0)
            throw new AmountMustBePositiveException(amount);

        BankTransactionData transactionData = new BankTransactionData(bankAccountData.getId(), -amount);
        bankAccountData.addTransaction(transactionData);
        playerData.setBalance(playerData.getBalance() + amount);

        BankService.saveBankAccount(bankData, bankAccountData);
        PlayerService.savePlayerData(playerData);

        logger.atInfo().log("Withdrew " + amount + " from bank account '" + bankAccountData.getAccountName()
                + "' at bank '" + bankData.getName() + "' by player '" + playerRef.getUsername() + "'");
    }

    /**
     * Get a bank account by name for a player at a bank
     *
     * @param bankId bank id
     * @param ownerId owner id
     * @param accountName account name
     * @return bank account data
     *
     * @throws BankNotFoundException if the bank does not exist
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
     *
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
}
