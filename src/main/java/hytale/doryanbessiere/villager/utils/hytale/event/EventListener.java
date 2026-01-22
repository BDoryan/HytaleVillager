package hytale.doryanbessiere.villager.utils.hytale.event;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;

public class EventListener {

    public static void registerListener(JavaPlugin plugin, Object... listeners) {
        for (Object listener : listeners) {
            EventBinder.registerAnnotatedHandlers(plugin.getEventRegistry(), listener);
        }
    }
}
