package hytale.doryanbessiere.fr.villager.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class BankManagementPageEventData {

    public static final String KEY_ACTION = "Action";
    public static final String KEY_BANK_NAME = "BankName";
    public static final String KEY_BANK_TYPE = "BankType";

    public static final BuilderCodec<BankManagementPageEventData> CODEC =
            BuilderCodec.builder(BankManagementPageEventData.class, BankManagementPageEventData::new)
                    .append(new KeyedCodec<>(KEY_ACTION, Codec.STRING),
                            (data, value) -> data.action = value,
                            data -> data.action)
                    .add()
                    .append(new KeyedCodec<>(KEY_BANK_NAME, Codec.STRING),
                            (data, value) -> data.bankName = value,
                            data -> data.bankName)
                    .add()
                    .append(new KeyedCodec<>(KEY_BANK_TYPE, Codec.STRING),
                            (data, value) -> data.bankType = value,
                            data -> data.bankType)
                    .add()
                    .build();

    private String action = "";
    private String bankName = "";
    private String bankType = "";

    public String getAction() {
        return action;
    }

    public String getBankName() {
        return bankName;
    }

    public String getBankType() {
        return bankType;
    }
}
