package hytale.doryanbessiere.fr.villager.exceptions.bank.account;

import hytale.doryanbessiere.fr.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.fr.villager.exceptions.bank.BankException;

public class BankAccountNotEmptyException extends BankException {

    private final BankAccountData bankAccountData;

    public BankAccountNotEmptyException(BankAccountData bankAccountData) {
        super("Bank account '" + bankAccountData.getAccountName() + "' is not empty.");

        this.bankAccountData = bankAccountData;
    }

    public BankAccountData getBankAccountData() {
        return bankAccountData;
    }
}
