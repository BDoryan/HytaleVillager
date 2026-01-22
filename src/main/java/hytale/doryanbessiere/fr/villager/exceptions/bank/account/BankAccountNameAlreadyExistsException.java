package hytale.doryanbessiere.fr.villager.exceptions.bank.account;

import hytale.doryanbessiere.fr.villager.exceptions.bank.BankException;

public class BankAccountNameAlreadyExistsException extends BankException {
    private final String bankName;

    public BankAccountNameAlreadyExistsException(String bankName) {
        super("Bank with name '" + bankName + "' not found.");

        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }
}
