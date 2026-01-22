package hytale.doryanbessiere.fr.villager.dto.economy;

public class BalanceData {

    private long balance = 0;

    public long getBalance() {
        return balance;
    }

    public void updateBalance(long amount) {
        this.balance += amount;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }
}
