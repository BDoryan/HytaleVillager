package hytale.doryanbessiere.villager.exceptions;

public class BankNotFoundException extends RuntimeException {
    private String bankName;

    public BankNotFoundException(String bankName) {
        super("Bank with name '" + bankName + "' not found.");

        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }
}
