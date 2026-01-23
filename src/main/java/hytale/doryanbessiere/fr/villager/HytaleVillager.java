package hytale.doryanbessiere.fr.villager;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import hytale.doryanbessiere.fr.villager.commands.bank.BankCommand;
import hytale.doryanbessiere.fr.villager.commands.debug.DebugCommand;
import hytale.doryanbessiere.fr.villager.commands.money.MoneyCommand;
import hytale.doryanbessiere.fr.villager.components.BankLinkComponent;
import hytale.doryanbessiere.fr.villager.listeners.PlayerConnectionListener;
import hytale.doryanbessiere.fr.villager.pages.BankAccountsPage;
import hytale.doryanbessiere.fr.villager.pages.BankAccountsPageSupplier;
import hytale.doryanbessiere.fr.utils.event.EventListener;

public class HytaleVillager extends JavaPlugin {

    private static final HytaleLogger logger = HytaleLogger.forEnclosingClass();
    private static HytaleVillager instance;

    public static HytaleVillager instance() {
        return instance;
    }

    public static HytaleLogger logger() {
        return logger;
    }

    public HytaleVillager(JavaPluginInit init) {
        super(init);

        logger.atInfo().log(init.getFile().toAbsolutePath().toString());
        instance = this;

        logger.atInfo().log("Starting Hytale Villager Plugin");
    }

    @Override
    protected void setup() {
        logger.atInfo().log("Starting registering commands");
        this.getCommandRegistry().registerCommand(new MoneyCommand());
        this.getCommandRegistry().registerCommand(new BankCommand());
        this.getCommandRegistry().registerCommand(new DebugCommand());

        logger.atInfo().log("Registering event listeners");
        EventListener.registerListener(this,
                new PlayerConnectionListener()
//                new PlayerInteractListener()
        );

        registerBankInteractions();

        logger.atInfo().log("Registering components");
        ComponentType<EntityStore, BankLinkComponent> type =
                getEntityStoreRegistry().registerComponent(BankLinkComponent.class, "BankLinkComponent", BankLinkComponent.CODEC);
        BankLinkComponent.setComponentType(type);
    }

    private void registerBankInteractions() {
        logger.atInfo().log("Registering bank interactions");
        OpenCustomUIInteraction.registerCustomPageSupplier(this,
                BankAccountsPage.class, "BankAccounts", new BankAccountsPageSupplier());
    }
}
