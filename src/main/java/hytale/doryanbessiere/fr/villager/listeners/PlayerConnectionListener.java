package hytale.doryanbessiere.fr.villager.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import hytale.doryanbessiere.fr.villager.dto.PlayerData;
import hytale.doryanbessiere.fr.villager.services.bank.BankService;
import hytale.doryanbessiere.fr.villager.services.PlayerService;
import hytale.doryanbessiere.fr.utils.event.OnEvent;

public class PlayerConnectionListener {

    private static boolean bankInteractionsRefreshed = false;

    @OnEvent(PlayerConnectEvent.class)
    public void onPlayerConnect(PlayerConnectEvent event) {
        PlayerData playerData = PlayerService.findPlayerDataOrCreate(event.getPlayerRef());

        if (!bankInteractionsRefreshed) {
            bankInteractionsRefreshed = true;
            BankService.ensureBankNpcInteractions();
        }
    }
}
