package dev.nxms.privatemessage.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Utility class for handling color code translation.
 * Supports both legacy color codes (&) and hex colors.
 */
public class ColorUtil {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
            LegacyComponentSerializer.legacyAmpersand();

    /**
     * Translates a string with color codes to a formatted string.
     * Supports & color codes.
     */
    public static String translate(String message) {
        if (message == null) return "";

        // Convert & codes to § codes for legacy compatibility
        return message.replace("&", "§");
    }

    /**
     * Translates a string with color codes to an Adventure Component.
     */
    public static Component translateToComponent(String message) {
        if (message == null) return Component.empty();

        return LEGACY_SERIALIZER.deserialize(message);
    }
}