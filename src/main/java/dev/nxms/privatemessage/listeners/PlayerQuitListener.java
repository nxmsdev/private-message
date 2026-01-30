package dev.nxms.privatemessage.listeners;

import dev.nxms.privatemessage.PrivateMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listens for player quit events to clean up conversation data.
 */
public class PlayerQuitListener implements Listener {

    private final PrivateMessage plugin;

    public PlayerQuitListener(PrivateMessage plugin) {
        this.plugin = plugin;
    }

    /**
     * Removes player data when they leave the server.
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getConversationManager().removePlayer(event.getPlayer().getUniqueId());
    }
}