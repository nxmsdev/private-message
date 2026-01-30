package dev.nxms.privatemessage.managers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages active conversations between players.
 * Tracks who each player last messaged and spy mode status.
 */
public class ConversationManager {

    private final Map<UUID, UUID> lastConversation;
    private final Set<UUID> spyEnabled;

    public ConversationManager() {
        this.lastConversation = new ConcurrentHashMap<>();
        this.spyEnabled = ConcurrentHashMap.newKeySet();
    }

    /**
     * Sets the last conversation partner for a player.
     */
    public void setLastConversation(UUID player, UUID target) {
        if (player == null || target == null) return;
        lastConversation.put(player, target);
    }

    /**
     * Gets the last conversation partner for a player.
     */
    public UUID getLastConversation(UUID player) {
        if (player == null) return null;
        return lastConversation.get(player);
    }

    /**
     * Checks if a player has a previous conversation partner.
     */
    public boolean hasLastConversation(UUID player) {
        if (player == null) return false;
        return lastConversation.containsKey(player);
    }

    /**
     * Removes all conversation data for a player.
     */
    public void removePlayer(UUID player) {
        if (player == null) return;

        lastConversation.remove(player);
        spyEnabled.remove(player);

        // Collect keys to remove to avoid ConcurrentModificationException
        List<UUID> toRemove = new ArrayList<>();
        for (Map.Entry<UUID, UUID> entry : lastConversation.entrySet()) {
            if (player.equals(entry.getValue())) {
                toRemove.add(entry.getKey());
            }
        }

        for (UUID key : toRemove) {
            lastConversation.remove(key);
        }
    }

    /**
     * Enables spy mode for a player.
     */
    public void enableSpy(UUID player) {
        if (player == null) return;
        spyEnabled.add(player);
    }

    /**
     * Disables spy mode for a player.
     */
    public void disableSpy(UUID player) {
        if (player == null) return;
        spyEnabled.remove(player);
    }

    /**
     * Checks if a player has spy mode enabled.
     */
    public boolean isSpyEnabled(UUID player) {
        if (player == null) return false;
        return spyEnabled.contains(player);
    }

    /**
     * Toggles spy mode for a player.
     */
    public boolean toggleSpy(UUID player) {
        if (player == null) return false;

        if (spyEnabled.contains(player)) {
            spyEnabled.remove(player);
            return false;
        } else {
            spyEnabled.add(player);
            return true;
        }
    }

    /**
     * Returns all players with spy mode enabled.
     */
    public Set<UUID> getSpyPlayers() {
        return new HashSet<>(spyEnabled);
    }
}