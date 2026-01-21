package hytale.doryanbessiere.villager.dto.economy.bank;

import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import hytale.doryanbessiere.villager.dto.economy.bank.account.BankAccountData;
import hytale.doryanbessiere.villager.services.bank.BankService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BankData {

    private final UUID id;
    private final BankType type;

    private String name;
    private Vector3f position;
    private Vector3f rotation;

    public BankData(String name, BankType type) {
        this.id = UUID.randomUUID();
        this.type = type;

        this.name = name;
    }

    /**
     * Apply the position and orientation of the player to the bank
     *
     * @param playerRef
     */
    public void applyPositionAndOrientation(PlayerRef playerRef) {
        this.position = playerRef.getTransform().getPosition().toVector3f();
        this.rotation = playerRef.getTransform().getRotation();
    }

    public long getTotalAccounts() {
        List<BankAccountData> accountsData = BankService.getAccountsById(this.id);
        return accountsData.size();
    }

    /**
     * Return the total balance of all accounts in this bank
     *
     * @return
     */
    public long getTotalBalance() {
        List<BankAccountData> accountsData = BankService.getAccountsById(this.id);
        return Arrays.stream(accountsData.toArray(new BankAccountData[0]))
                .mapToLong(BankAccountData::getBalance)
                .sum();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public BankType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String display_name) {
        this.name = display_name;
    }

    public UUID getId() {
        return id;
    }
}
