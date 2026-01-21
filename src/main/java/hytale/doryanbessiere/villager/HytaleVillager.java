package hytale.doryanbessiere.villager;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import hytale.doryanbessiere.villager.commands.bank.BankCommand;
import hytale.doryanbessiere.villager.commands.money.MoneyCommand;
import hytale.doryanbessiere.villager.listeners.PlayerConnectionListener;
import hytale.doryanbessiere.villager.repository.bank.BankRepository;
import hytale.doryanbessiere.villager.repository.PlayerRepository;
import hytale.doryanbessiere.villager.utils.event.EventListener;

public class HytaleVillager extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static HytaleVillager instance;

    public static PlayerRepository playerRepository;
    public static BankRepository bankRepository;

    public static HytaleVillager instance() {
        return instance;
    }

    public static HytaleLogger logger() {
        return LOGGER;
    }

    public HytaleVillager(JavaPluginInit init) {
        super(init);

        LOGGER.atInfo().log(init.getFile().toAbsolutePath().toString());
        instance = this;

        LOGGER.atInfo().log("Starting Hytale Villager Plugin");
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Starting registering commands");
        this.getCommandRegistry().registerCommand(new MoneyCommand());
        this.getCommandRegistry().registerCommand(new BankCommand());

        LOGGER.atInfo().log("Registering event listeners");
        EventListener.registerListener(this, new PlayerConnectionListener());
    }
}
