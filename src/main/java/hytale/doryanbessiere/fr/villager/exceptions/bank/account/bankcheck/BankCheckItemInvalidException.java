package hytale.doryanbessiere.fr.villager.exceptions.bank.account.bankcheck;

import com.hypixel.hytale.server.core.inventory.ItemStack;

public class BankCheckItemInvalidException extends RuntimeException {

    private final ItemStack itemStack;

    public BankCheckItemInvalidException(ItemStack itemStack) {
        super("Invalid bank check item: " + itemStack.getItemId());

        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
