package dev.nxms.privatemessage.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides tab completion for the /reply command.
 * Returns empty list as message content should not be auto-completed.
 */
public class ReplyTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {

        // Hide from players without permission
        if (!sender.hasPermission("privatemessage.reply")) {
            return new ArrayList<>();
        }

        // No completions for message content
        return new ArrayList<>();
    }
}