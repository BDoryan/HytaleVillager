package hytale.doryanbessiere.fr.villager.dto.economy.bank;

import hytale.doryanbessiere.fr.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.fr.villager.services.bank.BankService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BankData {

    private final UUID id;
    private final BankType type;

    private String name;
    private UUID entityId;

    public BankData(String name, BankType type) {
        this.id = UUID.randomUUID();
        this.type = type;

        this.name = name;
    }

    public long getTotalAccounts() {
        List<BankAccountData> accountsData = BankService.getAccountsById(this.id);
        return accountsData.size();
    }

    /**
     * Return the total balance of all accounts in this bank
     *
     * @return
     */
    public long getTotalBalance() {
        List<BankAccountData> accountsData = BankService.getAccountsById(this.id);
        return Arrays.stream(accountsData.toArray(new BankAccountData[0]))
                .mapToLong(BankAccountData::getBalance)
                .sum();
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public BankType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String display_name) {
        this.name = display_name;
    }

    public UUID getId() {
        return id;
    }
}
