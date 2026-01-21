package hytale.doryanbessiere.villager.repository.bank;

import hytale.doryanbessiere.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.villager.utils.IRepository;

import java.util.UUID;

public abstract class BankRepository implements IRepository<BankData> {

    public abstract BankAccountRepository getBankAccountRepository(UUID bankId);
    public abstract BankData findByName(String name);

}
