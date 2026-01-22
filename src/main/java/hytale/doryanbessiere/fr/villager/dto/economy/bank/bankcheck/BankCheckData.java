package hytale.doryanbessiere.fr.villager.dto.economy.bank.bankcheck;

import hytale.doryanbessiere.fr.villager.dto.economy.bank.account.BankTransactionData;

import java.util.UUID;

public class BankCheckData {

    private final String bankAccountId;
    private final long amount;

    public BankCheckData(String bankAccountId, long amount) {
        this.bankAccountId = bankAccountId;
        this.amount = amount;
    }

    public BankTransactionData toTransaction() {
        BankTransactionData bankTransactionData = new BankTransactionData(
                BankTransactionData.TransactionType.BANK_CHECK_REDEEM,
                UUID.fromString(this.bankAccountId),
                this.amount
        );
        bankTransactionData.originalAccountId(UUID.fromString(this.bankAccountId));

        return bankTransactionData;
    }

    public String getBankAccountId() {
        return bankAccountId;
    }

    public long getAmount() {
        return amount;
    }
}
