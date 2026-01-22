package hytale.doryanbessiere.fr.villager.items;

import com.hypixel.hytale.server.core.inventory.ItemStack;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.bankcheck.BankCheckData;
import hytale.doryanbessiere.fr.villager.exceptions.bank.account.bankcheck.BankCheckItemInvalidException;
import org.bson.BsonDocument;
import org.bson.BsonString;

public class BankCheckItem {

    /**
     * Create a bank check item stack
     *
     * @param bankAccountData
     * @param amount
     * @return
     */
    public static ItemStack createBankCheck(BankAccountData bankAccountData, long amount) {
        // Create bank check item with metadata
        BsonDocument metadata = new BsonDocument();
        metadata.append("bankAccountId", new BsonString(bankAccountData.getId().toString()));
        metadata.append("amount", new BsonString(Long.toString(amount)));

        // Return the item stack
        return new ItemStack("BankCheck", 1, metadata);
    }

    /**
     * Check if the item stack is a bank check
     *
     * @param itemStack
     * @return
     */
    public static boolean isBankCheck(ItemStack itemStack) {
        if (itemStack == null || !itemStack.getItemId().equals("BankCheck"))
            return false;

        BsonDocument metadata = itemStack.getMetadata();
        return metadata != null && metadata.containsKey("bankAccountId") && metadata.containsKey("amount");
    }

    /**
     * Get bank check data from item stack
     *
     * @param itemStack
     * @return
     */
    public static BankCheckData getBankCheck(ItemStack itemStack) {
        if (!isBankCheck(itemStack))
            throw new BankCheckItemInvalidException(itemStack);

        BsonDocument metadata = itemStack.getMetadata();
        String bankAccountId = metadata.getString("bankAccountId").getValue();
        long amount = Long.parseLong(metadata.getString("amount").getValue());
        return new BankCheckData(bankAccountId, amount);
    }
}
