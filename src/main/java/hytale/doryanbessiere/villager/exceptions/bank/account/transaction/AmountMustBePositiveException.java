package hytale.doryanbessiere.villager.exceptions.bank.account.transaction;

import hytale.doryanbessiere.villager.exceptions.bank.BankException;

public class AmountMustBePositiveException extends BankException {

    private final long amount;

    public AmountMustBePositiveException(long amount) {
        super("Invalid amount: " + amount + ". Amount must be greater than zero.");

        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }
}
