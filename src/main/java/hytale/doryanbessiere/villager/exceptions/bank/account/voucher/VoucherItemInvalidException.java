package hytale.doryanbessiere.villager.exceptions.bank.account.voucher;

import com.hypixel.hytale.server.core.inventory.ItemStack;

public class VoucherItemInvalidException extends RuntimeException {

    private final ItemStack itemStack;

    public VoucherItemInvalidException(ItemStack itemStack) {
        super("Invalid voucher item: " + itemStack.getItemId());

        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}
