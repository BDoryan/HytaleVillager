package hytale.doryanbessiere.villager.repository.adapter.file.bank;

import com.hypixel.hytale.logger.HytaleLogger;
import hytale.doryanbessiere.villager.HytaleVillager;
import hytale.doryanbessiere.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.villager.repository.bank.BankAccountRepository;
import hytale.doryanbessiere.villager.utils.FileStorageManager;

import java.util.List;
import java.util.UUID;

public class BankAccountRepositoryFile extends BankAccountRepository {

    private final HytaleLogger logger = HytaleLogger.forEnclosingClass();
    private final FileStorageManager<BankAccountData> storage;

    public BankAccountRepositoryFile(UUID bankId) {
        this.storage = new FileStorageManager<>("hytale-villager/banks/" + bankId + "/accounts/");
    }

    @Override
    public void create(BankAccountData data) {
        set(data);
    }

    @Override
    public void set(BankAccountData data) {
        this.storage.save(data, data.getId() + ".json");
    }

    @Override
    public void delete(BankAccountData data) {
        this.storage.delete(data.getId() + ".json");
    }

    @Override
    public List<BankAccountData> findAll() {
        return this.storage.loadAll(BankAccountData.class);
    }

    @Override
    public BankAccountData findById(Object search) {
        return this.storage.load(search.toString() + ".json", BankAccountData.class);
    }

    @Override
    public BankAccountData findAccountByName(UUID ownerId, String accountName) {
        logger.atInfo().log("Searching for account with name '" + accountName + "' for owner '" + ownerId + "'");

        List<BankAccountData> accounts = findAll();
        logger.atInfo().log("Total accounts found: " + accounts.size());

        return accounts.stream()
                .filter(accountData -> accountData.getOwnerId().equals(ownerId) && accountData.getAccountName().equalsIgnoreCase(accountName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean existById(Object search) {
        return findById(search) != null;
    }
}
