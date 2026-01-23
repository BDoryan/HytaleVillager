package hytale.doryanbessiere.fr.villager.listeners;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerInteractEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import hytale.doryanbessiere.fr.utils.PlayerUtils;
import hytale.doryanbessiere.fr.utils.event.OnEvent;
import hytale.doryanbessiere.fr.villager.HytaleVillager;
import hytale.doryanbessiere.fr.villager.components.BankLinkComponent;
import hytale.doryanbessiere.fr.villager.dto.economy.bank.BankData;
import hytale.doryanbessiere.fr.villager.services.bank.BankService;

import java.awt.*;

public class PlayerInteractListener {

    @OnEvent(PlayerInteractEvent.class)
    public static void onPlayerInteract(PlayerInteractEvent event) {
        HytaleVillager.logger().atInfo().log("PlayerInteractEvent triggered");

        Player player = event.getPlayer();

        Entity entity = event.getTargetEntity();
        if (entity == null)
            return;


        Ref<EntityStore> entityRef = entity.getReference();
        if (entityRef == null || !entityRef.isValid())
            return;

        Store<EntityStore> entityStore = entityRef.getStore();


        HytaleVillager.logger().atInfo().log("Player " + player.getDisplayName() +
                " interacted with entity " + entity.getReference());

        BankLinkComponent bankLinkComponent = entityStore.getComponent(entityRef, BankLinkComponent.getComponentType());
        if (bankLinkComponent != null) {
            BankData bankData = BankService.getBank(bankLinkComponent.getBankUuid());
            player.sendMessage(Message.raw("Opening bank interface for bank: " + bankData.getName())
                    .color(Color.GREEN));
        }
    }
}
