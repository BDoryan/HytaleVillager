package hytale.doryanbessiere.villager.items;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import hytale.doryanbessiere.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.villager.dto.economy.bank.voucher.PaymentVoucherData;
import hytale.doryanbessiere.villager.exceptions.bank.account.voucher.VoucherItemInvalidException;
import hytale.doryanbessiere.villager.utils.Utils;
import org.bson.BsonDocument;
import org.bson.BsonString;

public class PaymentVoucherItem {

    /**
     * Create a payment voucher item stack
     *
     * @param bankAccountData
     * @param amount
     * @return
     */
    public static ItemStack createPaymentVoucher(BankAccountData bankAccountData, long amount) {
        // Create voucher item with metadata
        BsonDocument metadata = new BsonDocument();
        metadata.append("bankAccountId", new BsonString(bankAccountData.getId().toString()));
        metadata.append("amount", new BsonString(Long.toString(amount)));

        // Return the item stack
        return new ItemStack("PaymentVoucher", 1, metadata);
    }

    /**
     * Check if the item stack is a payment voucher
     *
     * @param itemStack
     * @return
     */
    public static boolean isPaymentVoucher(ItemStack itemStack) {
        if (itemStack == null || !itemStack.getItemId().equals("PaymentVoucher")) {
            return false;
        }
        BsonDocument metadata = itemStack.getMetadata();
        return metadata != null && metadata.containsKey("bankAccountId") && metadata.containsKey("amount");
    }

    /**
     * Get payment voucher data from item stack
     *
     * @param itemStack
     * @return
     */
    public static PaymentVoucherData getPaymentVoucher(ItemStack itemStack) {
        if (!isPaymentVoucher(itemStack))
            throw new VoucherItemInvalidException(itemStack);

        BsonDocument metadata = itemStack.getMetadata();
        String bankAccountId = metadata.getString("bankAccountId").getValue();
        long amount = Long.parseLong(metadata.getString("amount").getValue());
        return new PaymentVoucherData(bankAccountId, amount);
    }
}
