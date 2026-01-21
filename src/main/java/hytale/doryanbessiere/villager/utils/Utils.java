package hytale.doryanbessiere.villager.utils;

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
