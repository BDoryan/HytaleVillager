package hytale.doryanbessiere.villager.dto.economy.bank.account;

import java.util.UUID;

public class BankTransactionData {

    private final UUID transactionId;
    private final UUID accountId;
    private final long amount;

    public BankTransactionData(UUID accountId, long amount) {
        this.transactionId = UUID.randomUUID();
        this.accountId = accountId;
        this.amount = amount;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public long getAmount() {
        return amount;
    }
}
