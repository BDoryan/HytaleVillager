package hytale.doryanbessiere.villager.services.bank;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import hytale.doryanbessiere.villager.exceptions.BankAccountNameIsAlreadyExistException;
import hytale.doryanbessiere.villager.repository.adapter.file.bank.BankRepositoryFile;
import hytale.doryanbessiere.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.villager.dto.economy.bank.BankType;
import hytale.doryanbessiere.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.villager.exceptions.BankNameAlreadyExistException;
import hytale.doryanbessiere.villager.exceptions.BankNotFoundException;
import hytale.doryanbessiere.villager.repository.bank.BankAccountRepository;
import hytale.doryanbessiere.villager.repository.bank.BankRepository;

import java.util.List;
import java.util.UUID;

public class BankService {

    private static final BankRepository bankRepository = new BankRepositoryFile();

    public static void createBank(String name, BankType type, PlayerRef playerRef) throws BankNameAlreadyExistException {
        BankData bankData = new BankData(name, type);
        if (bankRepository.findByName(bankData.getName()) != null)
            throw new BankNameAlreadyExistException(bankData.getName());

        bankData.applyPositionAndOrientation(playerRef);
        bankRepository.create(bankData);
    }

    public static List<BankAccountData> getAccountsById(UUID bankId) {
        BankData bankData = getBank(bankId);
        if (bankData == null)
            throw new BankNotFoundException(bankId.toString());

        BankAccountRepository bankAccountRepository = bankRepository.getBankAccountRepository(bankData.getId());
        return bankAccountRepository.findAll();
    }

    public static BankData openAccount(PlayerRef playerRef, String bankName, String accountName) throws BankAccountNameIsAlreadyExistException, BankNotFoundException {
        BankData bankData = getBankByName(bankName);

        BankAccountRepository bankAccountRepository = bankRepository.getBankAccountRepository(bankData.getId());
        BankAccountData bankAccountData = bankAccountRepository.findAccountByName(playerRef.getUuid(), accountName);
        if (bankAccountData != null)
            throw new BankAccountNameIsAlreadyExistException(accountName);

        bankAccountData = new BankAccountData(playerRef, accountName);
        bankAccountRepository.create(bankAccountData);

        return bankData;
    }

    public static BankAccountData getBankAccountByName(UUID bankId, UUID ownerId, String accountName) {
        BankData bankData = getBank(bankId);
        if (bankData == null)
            throw new BankNotFoundException(bankId.toString());

        BankAccountRepository bankAccountRepository = bankRepository.getBankAccountRepository(bankData.getId());
        BankAccountData bankAccountData = bankAccountRepository.findAccountByName(ownerId, accountName);
        return bankAccountData;
    }

    public static BankData getBankByName(String bankName) {
        BankData bankData = bankRepository.findByName(bankName);
        if (bankData == null)
            throw new BankNotFoundException(bankName);
        return bankData;
    }

    public static BankData getBank(UUID bankId) {
        return bankRepository.findById(bankId);
    }

    public static List<BankData> getBanks() {
        return bankRepository.findAll();
    }
}
