package dev.bowlos.chatos.managers;

import dev.bowlos.chatos.ChatOS;
import org.bukkit.entity.Player;

import java.util.*;


public final class MuteManager {

    private final ChatOS plugin;

    
    private final Map<UUID, String> mutedPlayers = new HashMap<>();

    public MuteManager(ChatOS plugin) { this.plugin = plugin; }

    public void mute(Player target, String reason) {
        mutedPlayers.put(target.getUniqueId(), reason == null ? "No reason provided." : reason);
    }

    public void unmute(Player target) {
        mutedPlayers.remove(target.getUniqueId());
    }

    public boolean isMuted(Player player) {
        return mutedPlayers.containsKey(player.getUniqueId());
    }

    public String getMuteReason(Player player) {
        return mutedPlayers.getOrDefault(player.getUniqueId(), "No reason provided.");
    }

    public int activeCount() { return mutedPlayers.size(); }

    public void handleDisconnect(Player player) {
        
    }
}
