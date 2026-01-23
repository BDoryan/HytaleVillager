package hytale.doryanbessiere.fr.villager.pages;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import hytale.doryanbessiere.fr.villager.components.BankLinkComponent;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.fr.villager.services.bank.BankService;
import javax.annotation.Nullable;

public class BankAccountsPageSupplier implements OpenCustomUIInteraction.CustomPageSupplier {

    private final HytaleLogger logger = HytaleLogger.getLogger();

    @Override
    @Nullable
    public CustomUIPage tryCreate(Ref<EntityStore> ref,
                                  ComponentAccessor<EntityStore> componentAccessor,
                                  PlayerRef playerRef,
                                  InteractionContext context) {
        logger.atInfo().log("Supplying BankAccountsPage for player: " + playerRef.getUsername());

        Ref<EntityStore> targetRef = context.getTargetEntity();
        if (targetRef == null || !targetRef.isValid())
            return null;

        BankLinkComponent bankLink = componentAccessor.getComponent(targetRef, BankLinkComponent.getComponentType());
        if (bankLink == null)
            return null;

        BankData bankData = BankService.getBank(bankLink.getBankUuid());
        if (bankData == null) {
            return null;
        }

        logger.atInfo().log("Creating BankAccountsPage for bank: " + bankData.getName());
        return new BankAccountsPage(playerRef, bankData);
    }
}
