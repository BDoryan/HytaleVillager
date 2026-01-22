package hytale.doryanbessiere.fr.villager.exceptions.bank;

public class BankNameAlreadyExistsException extends BankException {

    private final String bankName;

    public BankNameAlreadyExistsException(String bankName) {
        super("A bank with the name '" + bankName + "' already exists.");

        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }
}
