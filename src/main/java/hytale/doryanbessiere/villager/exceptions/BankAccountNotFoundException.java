package hytale.doryanbessiere.villager.exceptions;

public class BankAccountNotFoundException extends RuntimeException {
    private String bankAccountName;

    public BankAccountNotFoundException(String bankAccountName) {
        super("Bank with name '" + bankAccountName + "' not found.");

        this.bankAccountName = bankAccountName;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }
}
