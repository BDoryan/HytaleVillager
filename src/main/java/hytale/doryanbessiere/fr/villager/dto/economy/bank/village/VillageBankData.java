package hytale.doryanbessiere.fr.villager.dto.economy.bank.village;

import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankType;

public class VillageBankData extends BankData {

    private final String villageId;

    public VillageBankData(String name, String villageId) {
        super(name, BankType.GOVERNMENT);

        this.villageId = villageId;
    }

    public String getVillageId() {
        return villageId;
    }
}
