package dev.nxms.privatemessage.tabcomplete;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides tab completion for the /message command.
 */
public class MessageTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {

        // Hide from players without permission
        if (!sender.hasPermission("privatemessage.message")) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        // First argument - player names
        if (args.length == 1) {
            String input = args[0].toLowerCase();

            for (Player player : Bukkit.getOnlinePlayers()) {
                // Don't suggest the sender's own name
                if (sender instanceof Player && player.equals(sender)) {
                    continue;
                }

                if (player.getName().toLowerCase().startsWith(input)) {
                    completions.add(player.getName());
                }
            }
        }

        return completions;
    }
}