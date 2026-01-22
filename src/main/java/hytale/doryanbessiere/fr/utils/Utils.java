package hytale.doryanbessiere.fr.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.util.UUID;

public class Utils {

    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String snakeCase(String s) {
        String[] parts = s.split("_");
        StringBuilder camelCaseString = new StringBuilder();

        for (String part : parts) {
            if (camelCaseString.isEmpty()) {
                camelCaseString.append(part.toLowerCase());
            } else {
                camelCaseString.append(part.substring(0, 1).toUpperCase());
                camelCaseString.append(part.substring(1).toLowerCase());
            }
        }
        return camelCaseString.toString();
    }
}
