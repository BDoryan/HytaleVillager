package hytale.doryanbessiere.villager.repository.adapter.file.bank;

import hytale.doryanbessiere.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.villager.repository.bank.BankAccountRepository;
import hytale.doryanbessiere.villager.repository.bank.BankRepository;
import hytale.doryanbessiere.villager.utils.FileStorageManager;
import hytale.doryanbessiere.villager.utils.Utils;

import java.util.List;
import java.util.UUID;

public class BankRepositoryFile extends BankRepository {


    private final FileStorageManager<BankData> storage;

    public BankRepositoryFile() {
        this.storage = new FileStorageManager<>("hytale-villager/banks/");
    }

    @Override
    public void create(BankData data) {
        if (existById(data.getId()))
            return;
        set(data);
    }

    @Override
    public void set(BankData data) {
        this.storage.save(data, data.getId() + "/metadata.json");
    }

    @Override
    public void delete(BankData data) {
        this.storage.deleteStorageDir();
    }

    @Override
    public List<BankData> findAll() {
        return this.storage.loadAll(BankData.class);
    }

    @Override
    public BankData findById(Object search) {
        search = Utils.snakeCase(search.toString());

        return this.storage.load(search + "/metadata.json", BankData.class);
    }

    @Override
    public BankData findByName(String name) {
        return findAll().stream()
                .filter(bankData -> bankData.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean existById(Object search) {
        return findById(search) != null;
    }

    public BankAccountRepository getBankAccountRepository(UUID bankId) {
        return new BankAccountRepositoryFile(bankId);
    }
}
