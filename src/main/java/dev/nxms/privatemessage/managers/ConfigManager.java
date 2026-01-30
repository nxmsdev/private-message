package dev.nxms.privatemessage.managers;

import dev.nxms.privatemessage.PrivateMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Manages the main configuration file for the plugin.
 * Handles loading, saving, and accessing configuration values.
 */
public class ConfigManager {

    private final PrivateMessage plugin;
    private FileConfiguration config;

    public ConfigManager(PrivateMessage plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    /**
     * Loads or creates the default configuration file.
     */
    private void loadConfig() {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();

        plugin.getLogger().info("Configuration has been loaded.");
    }

    /**
     * Reloads the configuration from disk.
     */
    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        plugin.getLogger().info("Configuration has been reloaded.");
    }

    /**
     * Returns the configured language code.
     */
    public String getLanguage() {
        return config.getString("language", "en");
    }

    /**
     * Returns the list of blocked names that cannot receive messages.
     */
    public List<String> getBlockedNames() {
        return config.getStringList("blocked-names");
    }

    /**
     * Checks if a given name is in the blocked names list.
     */
    public boolean isBlockedName(String name) {
        return getBlockedNames().stream()
                .anyMatch(blocked -> blocked.equalsIgnoreCase(name));
    }
}