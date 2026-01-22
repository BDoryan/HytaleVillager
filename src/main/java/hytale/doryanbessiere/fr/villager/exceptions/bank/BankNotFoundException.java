package hytale.doryanbessiere.fr.villager.exceptions.bank;

public class BankNotFoundException extends BankException {
    private final String bankName;

    public BankNotFoundException(String bankName) {
        super("Bank with name '" + bankName + "' not found.");

        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }
}
