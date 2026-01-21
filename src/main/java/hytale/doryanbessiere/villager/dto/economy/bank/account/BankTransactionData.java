package hytale.doryanbessiere.villager.dto.economy.bank.account;

import java.time.LocalDateTime;
import java.util.UUID;

public class BankTransactionData {


    private final UUID transactionId;
    private final UUID accountId;
    private final long amount;

    private UUID originalAccountId;
    private LocalDateTime createdAt;

    public BankTransactionData(UUID accountId, long amount) {
        this.transactionId = UUID.randomUUID();
        this.accountId = accountId;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    public BankTransactionData originalAccountId(UUID accountId) {
        this.originalAccountId = accountId;
        return this;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
