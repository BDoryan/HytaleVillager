package hytale.doryanbessiere.fr.utils.event;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;

/**
 * Utility class to register event listeners.
 */
public class EventListener {

    private static final HytaleLogger logger = HytaleLogger.forEnclosingClass();

    public static void registerListener(JavaPlugin plugin, Object... listeners) {
        logger.atInfo().log("Registering " + listeners.length + " event listeners");
        for (Object listener : listeners) {
            EventBinder.registerAnnotatedHandlers(plugin.getEventRegistry(), listener);
            logger.atInfo().log("Registered event listener: " + listener.getClass().getName());
        }
    }
}
