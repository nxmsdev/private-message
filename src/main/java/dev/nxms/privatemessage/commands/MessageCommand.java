package dev.nxms.privatemessage.commands;

import dev.nxms.privatemessage.PrivateMessage;
import dev.nxms.privatemessage.managers.ConversationManager;
import dev.nxms.privatemessage.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Handles the /message command for sending private messages to other players.
 */
public class MessageCommand implements CommandExecutor {

    private final PrivateMessage plugin;
    private final MessageManager messages;
    private final ConversationManager conversations;

    public MessageCommand(PrivateMessage plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessageManager();
        this.conversations = plugin.getConversationManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("privatemessage.message")) {
            sender.sendMessage(messages.getMessage("no-permission"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getMessage("player-only"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(messages.getMessage("usage-message"));
            return true;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(messages.getMessage("player-not-found", "{player}", targetName));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            sender.sendMessage(messages.getMessage("cannot-message-self"));
            return true;
        }

        if (plugin.getConfigManager().isBlockedName(target.getName())) {
            sender.sendMessage(messages.getMessage("blocked-name"));
            return true;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]);
            if (i < args.length - 1) {
                messageBuilder.append(" ");
            }
        }
        String message = messageBuilder.toString();

        sendPrivateMessage(player, target, message);

        return true;
    }

    /**
     * Sends a private message from sender to receiver and updates conversation tracking.
     */
    private void sendPrivateMessage(Player sender, Player receiver, String message) {
        sender.sendMessage(messages.getMessage("message-sent",
                "{sender}", sender.getName(),
                "{receiver}", receiver.getName(),
                "{message}", message));

        receiver.sendMessage(messages.getMessage("message-received",
                "{sender}", sender.getName(),
                "{receiver}", receiver.getName(),
                "{message}", message));

        conversations.setLastConversation(sender.getUniqueId(), receiver.getUniqueId());
        conversations.setLastConversation(receiver.getUniqueId(), sender.getUniqueId());

        notifySpies(sender, receiver, message);
    }

    /**
     * Sends the message to all players with spy mode enabled.
     */
    private void notifySpies(Player sender, Player receiver, String message) {
        String spyMessage = messages.getMessage("spy-format",
                "{sender}", sender.getName(),
                "{receiver}", receiver.getName(),
                "{message}", message);

        for (UUID spyUUID : conversations.getSpyPlayers()) {
            if (spyUUID == null) continue;
            if (spyUUID.equals(sender.getUniqueId()) || spyUUID.equals(receiver.getUniqueId())) {
                continue;
            }

            Player spy = Bukkit.getPlayer(spyUUID);
            if (spy != null && spy.isOnline()) {
                spy.sendMessage(spyMessage);
            }
        }
    }
}