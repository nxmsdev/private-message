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
 * Handles the /reply command for quickly responding to the last private message.
 */
public class ReplyCommand implements CommandExecutor {

    private final PrivateMessage plugin;
    private final MessageManager messages;
    private final ConversationManager conversations;

    public ReplyCommand(PrivateMessage plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessageManager();
        this.conversations = plugin.getConversationManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("privatemessage.reply")) {
            sender.sendMessage(messages.getMessage("no-permission"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getMessage("player-only"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(messages.getMessage("usage-reply"));
            return true;
        }

        if (!conversations.hasLastConversation(player.getUniqueId())) {
            sender.sendMessage(messages.getMessage("no-one-to-reply"));
            return true;
        }

        UUID targetUUID = conversations.getLastConversation(player.getUniqueId());
        if (targetUUID == null) {
            sender.sendMessage(messages.getMessage("no-one-to-reply"));
            return true;
        }

        Player target = Bukkit.getPlayer(targetUUID);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(messages.getMessage("player-not-found", "{player}", "unknown"));
            return true;
        }

        String message = String.join(" ", args);

        sendReply(player, target, message);

        return true;
    }

    /**
     * Sends a reply message to the target player.
     */
    private void sendReply(Player sender, Player receiver, String message) {
        sender.sendMessage(messages.getMessage("reply-sent",
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