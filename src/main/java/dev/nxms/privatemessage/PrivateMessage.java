package dev.nxms.privatemessage;

import dev.nxms.privatemessage.commands.MessageCommand;
import dev.nxms.privatemessage.commands.PrivateMessageCommand;
import dev.nxms.privatemessage.commands.ReplyCommand;
import dev.nxms.privatemessage.listeners.PlayerQuitListener;
import dev.nxms.privatemessage.managers.ConfigManager;
import dev.nxms.privatemessage.managers.ConversationManager;
import dev.nxms.privatemessage.managers.MessageManager;
import dev.nxms.privatemessage.tabcomplete.MessageTabCompleter;
import dev.nxms.privatemessage.tabcomplete.PrivateMessageTabCompleter;
import dev.nxms.privatemessage.tabcomplete.ReplyTabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for PrivateMessage plugin.
 * Handles initialization and registration of all components.
 */
public class PrivateMessage extends JavaPlugin {

    private static PrivateMessage instance;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private ConversationManager conversationManager;

    @Override
    public void onEnable() {
        instance = this;

        initializeManagers();
        registerCommands();
        registerListeners();

        getLogger().info("PrivateMessage has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("PrivateMessage has been disabled.");
    }

    /**
     * Initializes all manager classes responsible for handling configuration,
     * messages, and conversations.
     */
    private void initializeManagers() {
        this.configManager = new ConfigManager(this);
        this.messageManager = new MessageManager(this);
        this.conversationManager = new ConversationManager();

        getLogger().info("Managers has been initialized.");
    }

    /**
     * Registers all commands and their tab completers.
     */
    private void registerCommands() {
        MessageCommand messageCommand = new MessageCommand(this);
        ReplyCommand replyCommand = new ReplyCommand(this);
        PrivateMessageCommand privateMessageCommand = new PrivateMessageCommand(this);

        getCommand("message").setExecutor(messageCommand);
        getCommand("message").setTabCompleter(new MessageTabCompleter());

        getCommand("reply").setExecutor(replyCommand);
        getCommand("reply").setTabCompleter(new ReplyTabCompleter());

        getCommand("privatemessage").setExecutor(privateMessageCommand);
        getCommand("privatemessage").setTabCompleter(new PrivateMessageTabCompleter());

        getLogger().info("Commands has been registered.");
    }

    /**
     * Registers all event listeners.
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        getLogger().info("Listeners has been registered.");
    }

    /**
     * Reloads all configuration files and messages.
     */
    public void reloadAllConfigs() {
        configManager.reloadConfig();
        messageManager.reloadMessages();

        getLogger().info("PrivateMessage plugin has been reloaded.");
    }

    public static PrivateMessage getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ConversationManager getConversationManager() {
        return conversationManager;
    }
}