package dev.bowlos.chatos.managers;

import dev.bowlos.chatos.ChatOS;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;


public final class StaffModeManager {

    private final ChatOS plugin;
    private final Set<UUID> activePlayers = new HashSet<>();

    public StaffModeManager(ChatOS plugin) { this.plugin = plugin; }

    
    public boolean toggle(Player player) {
        if (activePlayers.contains(player.getUniqueId())) {
            disable(player);
            return false;
        }
        enable(player);
        return true;
    }

    public boolean isInStaffMode(Player player) {
        return activePlayers.contains(player.getUniqueId());
    }

    public int activeCount() { return activePlayers.size(); }

    

    private void enable(Player player) {
        activePlayers.add(player.getUniqueId());

        if (plugin.getConfigUtil().isStaffModeVanish()
                && player.hasPermission("chatos.staffmode.vanish")) {
            
            for (Player other : plugin.getServer().getOnlinePlayers()) {
                if (!other.hasPermission("chatos.staffmode") && !other.equals(player)) {
                    other.hidePlayer(plugin, player);
                }
            }
        }

        if (plugin.getConfigUtil().isStaffModeGod()
                && player.hasPermission("chatos.staffmode.god")) {
            player.setInvulnerable(true);
        }
    }

    private void disable(Player player) {
        activePlayers.remove(player.getUniqueId());

        
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            other.showPlayer(plugin, player);
        }

        player.setInvulnerable(false);
    }

    public void handleDisconnect(Player player) {
        if (activePlayers.contains(player.getUniqueId())) {
            
            for (Player other : plugin.getServer().getOnlinePlayers()) {
                other.showPlayer(plugin, player);
            }
            activePlayers.remove(player.getUniqueId());
        }
    }
}
