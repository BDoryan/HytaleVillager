package hytale.doryanbessiere.fr.utils.event;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;

import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * Utility class to bind event handlers annotated with @OnEvent.
 */
public final class EventBinder {

    private static final HytaleLogger logger = HytaleLogger.forEnclosingClass();

    private EventBinder() {
    }

    public static void registerAnnotatedHandlers(EventRegistry registry, Object listener) {
        Class<?> clazz = listener.getClass();

        for (Method m : clazz.getDeclaredMethods()) {
            OnEvent ann = m.getAnnotation(OnEvent.class);
            if (ann == null) continue;

            Class<?> eventType = ann.value();
            Class<?>[] params = m.getParameterTypes();
            if (params.length != 1 || !params[0].isAssignableFrom(eventType)) {
                throw new IllegalArgumentException(
                        "Méthode " + m.getName() + " invalide: doit prendre exactement 1 paramètre compatible avec " + eventType.getName()
                );
            }

            m.setAccessible(true);

            Consumer<Object> handler = (evt) -> {
                try {
                    m.invoke(listener, evt);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            // Register
            register(registry, eventType, handler);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void register(EventRegistry registry, Class eventType, Consumer handler) {
        logger.atInfo().log("Registered event handler for event: " + eventType.getName());
        registry.register(eventType, handler);
    }
}
