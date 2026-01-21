package hytale.doryanbessiere.villager.dto.economy.bank.voucher;

import hytale.doryanbessiere.villager.dto.economy.bank.account.BankTransactionData;

import java.util.UUID;

public class PaymentVoucherData {

    private final String bankAccountId;
    private final long amount;

    public PaymentVoucherData(String bankAccountId, long amount) {
        this.bankAccountId = bankAccountId;
        this.amount = amount;
    }

    public BankTransactionData toTransaction() {
        BankTransactionData bankTransactionData = new BankTransactionData(
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
