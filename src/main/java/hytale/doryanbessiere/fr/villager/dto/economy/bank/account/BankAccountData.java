package hytale.doryanbessiere.fr.villager.dto.economy.bank.account;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.transaction.InsufficientFundsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BankAccountData {

    private final UUID id;
    private final UUID ownerId;
    private final List<BankTransactionData> transactions = new ArrayList<>();

    private String accountName;

    public BankAccountData(PlayerRef playerRef, String accountName) {
        this.id = UUID.randomUUID();
        this.ownerId = playerRef.getUuid();

        this.accountName = accountName;
    }

    /**
     * Add a transaction to the account (deposit/withdrawal), it's not a logic system here
     *
     * @param transaction
     */
    public void addTransaction(BankTransactionData transaction) {
        // Check for insufficient funds (withdrawal)
        if(transaction.getAmount() < 0
                && getBalance() + transaction.getAmount() < 0) {
            throw new InsufficientFundsException(this.getBalance(), transaction.getAmount());
        }

        this.transactions.add(transaction);
    }

    public List<BankTransactionData> getTransactions() {
        return transactions;
    }

    public long getBalance() {
        return Arrays.stream(transactions.toArray(new BankTransactionData[0]))
                .mapToLong(BankTransactionData::getAmount)
                .sum();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getAccountName() {
        return accountName;
    }
}
