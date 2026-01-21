package hytale.doryanbessiere.villager.exceptions;

public class BankAccountNameIsAlreadyExistException extends RuntimeException {
    private String bankName;

    public BankAccountNameIsAlreadyExistException(String bankName) {
        super("Bank with name '" + bankName + "' not found.");

        this.bankName = bankName;
    }

    public String getBankName() {
        return bankName;
    }
}
