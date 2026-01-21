package hytale.doryanbessiere.villager.dto.economy.bank.account;

import com.hypixel.hytale.server.core.universe.PlayerRef;

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

    public void addTransaction(BankTransactionData transaction) {
        this.transactions.add(transaction);
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
