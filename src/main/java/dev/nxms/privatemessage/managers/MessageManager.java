package dev.nxms.privatemessage.managers;

import dev.nxms.privatemessage.PrivateMessage;
import dev.nxms.privatemessage.utils.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages localized messages loaded from language files.
 * Supports multiple languages and placeholder replacement.
 */
public class MessageManager {

    private final PrivateMessage plugin;
    private FileConfiguration messagesConfig;

    // Pattern to match {placeholder} format
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)}");

    public MessageManager(PrivateMessage plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    /**
     * Loads messages from the appropriate language file based on configuration.
     */
    private void loadMessages() {
        String currentLanguage = plugin.getConfigManager().getLanguage();
        String fileName = "messages_" + currentLanguage + ".yml";

        File messagesFile = new File(plugin.getDataFolder(), fileName);

        if (!messagesFile.exists()) {
            plugin.saveResource("messages_en.yml", false);
            plugin.getLogger().warning("Couldn't find messages file! Loading default messages file.");
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        InputStream defaultStream = plugin.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            messagesConfig.setDefaults(defaultConfig);
        }

        plugin.getLogger().info("Messages has been loaded.");
    }

    /**
     * Reloads messages from disk with the current language setting.
     */
    public void reloadMessages() {
        loadMessages();
    }

    /**
     * Gets a raw message string from the messages file.
     */
    public String getRawMessage(String key) {
        return messagesConfig.getString(key, "");
    }

    /**
     * Replaces all {key} placeholders with values from messages file.
     */
    private String replacePlaceholders(String message) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(message);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String value = getRawMessage(placeholder);

            // Only replace if key exists in config and is not empty
            if (!value.isEmpty()) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(value));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Gets a formatted message with color codes and all placeholders replaced.
     */
    public String getMessage(String key) {
        String message = getRawMessage(key);
        message = replacePlaceholders(message);
        return ColorUtil.translate(message);
    }

    /**
     * Gets a formatted message with custom placeholders replaced.
     */
    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        if (replacements != null) {
            for (int i = 0; i < replacements.length - 1; i += 2) {
                if (replacements[i] != null && replacements[i + 1] != null) {
                    message = message.replace(replacements[i], replacements[i + 1]);
                }
            }
        }
        return message;
    }
}