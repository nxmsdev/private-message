package dev.nxms.privatemessage.commands;

import dev.nxms.privatemessage.PrivateMessage;
import dev.nxms.privatemessage.managers.ConversationManager;
import dev.nxms.privatemessage.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the /privatemessage admin command with subcommands for spy, reload and help.
 */
public class PrivateMessageCommand implements CommandExecutor {

    private final PrivateMessage plugin;
    private final MessageManager messages;
    private final ConversationManager conversations;

    public PrivateMessageCommand(PrivateMessage plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessageManager();
        this.conversations = plugin.getConversationManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("privatemessage.command") && !sender.hasPermission("privatemessage.admin")) {
            sender.sendMessage(messages.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0) {
            handleHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "spy" -> handleSpy(sender, args);
            case "reload" -> handleReload(sender);
            case "help" -> handleHelp(sender);
            default -> sender.sendMessage(messages.getMessage("unknown-subcommand"));
        }

        return true;
    }

    /**
     * Handles the spy subcommand for toggling spy mode.
     */
    private void handleSpy(CommandSender sender, String[] args) {
        if (!sender.hasPermission("privatemessage.spy") && !sender.hasPermission("privatemessage.admin")) {
            sender.sendMessage(messages.getMessage("no-permission"));
            return;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getMessage("player-only"));
            return;
        }

        if (args.length < 2) {
            boolean newState = conversations.toggleSpy(player.getUniqueId());
            if (newState) {
                sender.sendMessage(messages.getMessage("spy-enabled"));
            } else {
                sender.sendMessage(messages.getMessage("spy-disabled"));
            }
            return;
        }

        String value = args[1].toLowerCase();

        switch (value) {
            case "on" -> {
                conversations.enableSpy(player.getUniqueId());
                sender.sendMessage(messages.getMessage("spy-enabled"));
            }
            case "off" -> {
                conversations.disableSpy(player.getUniqueId());
                sender.sendMessage(messages.getMessage("spy-disabled"));
            }
            default -> sender.sendMessage(messages.getMessage("spy-invalid-argument"));
        }
    }

    /**
     * Handles the reload subcommand for reloading configuration files.
     */
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("privatemessage.reload") && !sender.hasPermission("privatemessage.admin")) {
            sender.sendMessage(messages.getMessage("no-permission"));
            return;
        }

        plugin.reloadAllConfigs();
        sender.sendMessage(messages.getMessage("config-reloaded"));
    }

    /**
     * Handles the help subcommand for displaying all available commands.
     */
    private void handleHelp(CommandSender sender) {
        sender.sendMessage(messages.getMessage("help-header"));
        sender.sendMessage(messages.getMessage("help-message"));
        sender.sendMessage(messages.getMessage("help-reply"));

        if (sender.hasPermission("privatemessage.spy") || sender.hasPermission("privatemessage.admin")) {
            sender.sendMessage(messages.getMessage("help-spy"));
        }

        if (sender.hasPermission("privatemessage.reload") || sender.hasPermission("privatemessage.admin")) {
            sender.sendMessage(messages.getMessage("help-reload"));
        }

        sender.sendMessage(messages.getMessage("help-footer"));
    }
}