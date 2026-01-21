package hytale.doryanbessiere.villager.exceptions;

public class BankNameAlreadyExistException extends RuntimeException {

    private String bankName;

    public BankNameAlreadyExistException(String bankName) {
        super("A bank with the name '" + bankName + "' already exists.");

        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }
}
