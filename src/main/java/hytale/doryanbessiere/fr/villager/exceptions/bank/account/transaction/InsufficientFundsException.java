package hytale.doryanbessiere.fr.villager.exceptions.bank.account.transaction;

import hytale.doryanbessiere.fr.villager.exceptions.bank.BankException;

public class InsufficientFundsException extends BankException {

    private final long balance;
    private final long amount;

    public InsufficientFundsException(long balance, long amount) {
        super("Insufficient funds: balance is " + balance + ", attempted transaction amount is " + amount + ".");

        this.balance = balance;
        this.amount = amount;
    }

    public long getBalance() {
        return balance;
    }

    public long getAmount() {
        return amount;
    }
}
