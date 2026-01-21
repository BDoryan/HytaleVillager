package hytale.doryanbessiere.villager.exceptions.bank.account;

import hytale.doryanbessiere.villager.exceptions.bank.BankException;

public class BankAccountNotFoundException extends BankException {
    private final String bankAccountName;

    public BankAccountNotFoundException(String bankAccountName) {
        super("Bank with name '" + bankAccountName + "' not found.");

        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }
}
