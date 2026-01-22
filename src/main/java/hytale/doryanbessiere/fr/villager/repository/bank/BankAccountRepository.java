package hytale.doryanbessiere.fr.villager.repository.bank;

import hytale.doryanbessiere.fr.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.fr.utils.IRepository;

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
