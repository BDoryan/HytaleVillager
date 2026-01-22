package hytale.doryanbessiere.fr.villager.repository.bank;

import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.fr.utils.IRepository;

import java.util.UUID;

public abstract class BankRepository implements IRepository<BankData> {

    public abstract BankAccountRepository getBankAccountRepository(UUID bankId);
    public abstract BankData findByName(String name);

}
