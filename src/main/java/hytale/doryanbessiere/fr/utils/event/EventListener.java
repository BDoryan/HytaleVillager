package hytale.doryanbessiere.fr.utils.event;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;

/**
 * Utility class to register event listeners.
 */
public class EventListener {

    public static void registerListener(JavaPlugin plugin, Object... listeners) {
        for (Object listener : listeners) {
            EventBinder.registerAnnotatedHandlers(plugin.getEventRegistry(), listener);
        }
    }
}
