package dev.nxms.privatemessage.managers;

import dev.nxms.privatemessage.PrivateMessage;
import dev.nxms.privatemessage.utils.ColorUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Manages localized messages loaded from language files.
 * Supports multiple languages and placeholder replacement.
 */
public class MessageManager {

    private final PrivateMessage plugin;
    private FileConfiguration messagesConfig;
    private String currentLanguage;

    public MessageManager(PrivateMessage plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    /**
     * Loads messages from the appropriate language file based on configuration.
     */
    private void loadMessages() {
        currentLanguage = plugin.getConfigManager().getLanguage();
        String fileName = "messages_" + currentLanguage + ".yml";

        File messagesFile = new File(plugin.getDataFolder(), fileName);

        if (!messagesFile.exists()) {
            plugin.saveResource(fileName, false);
            plugin.getLogger().warning("Couldn't find messages file! Loading default messages file.");
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Load defaults from jar to ensure all keys exist
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
        return messagesConfig.getString(key, "&cMissing message: " + key);
    }

    /**
     * Gets a formatted message with color codes translated.
     */
    public String getMessage(String key) {
        String prefix = getRawMessage("prefix");
        String message = getRawMessage(key);
        return ColorUtil.translate(prefix + message);
    }

    /**
     * Gets a formatted message without prefix.
     */
    public String getMessageNoPrefix(String key) {
        return ColorUtil.translate(getRawMessage(key));
    }

    /**
     * Gets a formatted message with placeholders replaced.
     */
    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }

    /**
     * Gets a formatted message without prefix with placeholders replaced.
     */
    public String getMessageNoPrefix(String key, String... replacements) {
        String message = getMessageNoPrefix(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }
}