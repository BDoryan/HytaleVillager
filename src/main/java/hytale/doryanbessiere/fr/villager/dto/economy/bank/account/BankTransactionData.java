package hytale.doryanbessiere.fr.villager.dto.economy.bank.account;

import java.time.LocalDateTime;
import java.util.UUID;

public class BankTransactionData {

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        BANK_CHECK_REDEEM,
        BANK_CHECK_CREATE
    }

    private final TransactionType type;
    private final UUID transactionId;
    private final UUID accountId;
    private final long amount;

    private UUID originalAccountId;
    private LocalDateTime createdAt;

    public BankTransactionData(TransactionType type, UUID accountId, long amount) {
        this.type = type;
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

    public UUID getOriginalAccountId() {
        return originalAccountId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
