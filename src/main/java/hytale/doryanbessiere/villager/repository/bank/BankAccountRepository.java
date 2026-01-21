package hytale.doryanbessiere.villager.repository.bank;

import hytale.doryanbessiere.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.villager.utils.IRepository;

import java.util.UUID;

public abstract class BankAccountRepository implements IRepository<BankAccountData> {

    /**
     * Find an account by its name and owner ID
     *
     * @param ownerId
     * @param accountName
     * @return
     */
    public abstract BankAccountData findAccountByName(UUID ownerId, String accountName);

}
