package hytale.doryanbessiere.fr.villager.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class BankAccountsPageEventData {

    public static final String KEY_ACTION = "Action";
    public static final String KEY_ACCOUNT_NAME = "AccountName";
    public static final String KEY_AMOUNT = "Amount";

    public static final BuilderCodec<BankAccountsPageEventData> CODEC =
            BuilderCodec.builder(BankAccountsPageEventData.class, BankAccountsPageEventData::new)
                    .append(new KeyedCodec<>(KEY_ACTION, Codec.STRING),
                            (data, value) -> data.action = value,
                            data -> data.action)
                    .add()
                    .append(new KeyedCodec<>(KEY_ACCOUNT_NAME, Codec.STRING),
                            (data, value) -> data.accountName = value,
                            data -> data.accountName)
                    .add()
                    .append(new KeyedCodec<>(KEY_AMOUNT, Codec.STRING),
                            (data, value) -> data.amount = value,
                            data -> data.amount)
                    .add()
                    .build();

    private String action = "";
    private String accountName = "";
    private String amount = "";

    public String getAction() {
        return action;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAmount() {
        return amount;
    }
}
