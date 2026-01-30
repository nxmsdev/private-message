package dev.nxms.privatemessage.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides tab completion for the /privatemessage admin command.
 * Only shows subcommands the player has permission to use.
 */
public class PrivateMessageTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {

        // Hide from players without any permission
        if (!sender.hasPermission("privatemessage.command") && !sender.hasPermission("privatemessage.admin")) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            // Add subcommands based on permissions
            if (sender.hasPermission("privatemessage.spy") || sender.hasPermission("privatemessage.admin")) {
                if ("spy".startsWith(input)) {
                    completions.add("spy");
                }
            }

            if (sender.hasPermission("privatemessage.reload") || sender.hasPermission("privatemessage.admin")) {
                if ("reload".startsWith(input)) {
                    completions.add("reload");
                }
            }

            if (sender.hasPermission("privatemessage.help") || sender.hasPermission("privatemessage.admin")) {
                if ("help".startsWith(input)) {
                    completions.add("help");
                }
            }

        } else if (args.length == 2) {
            // Second argument for spy command
            if (args[0].equalsIgnoreCase("spy")) {
                if (sender.hasPermission("privatemessage.spy") || sender.hasPermission("privatemessage.admin")) {
                    String input = args[1].toLowerCase();

                    if ("on".startsWith(input)) completions.add("on");
                    if ("off".startsWith(input)) completions.add("off");
                }
            }
        }

        return completions;
    }
}