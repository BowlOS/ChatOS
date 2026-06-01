package dev.bowlos.chatos.managers;

import dev.bowlos.chatos.ChatOS;
import org.bukkit.entity.Player;

import java.util.*;


public final class SpyManager {

    private final ChatOS plugin;
    private final Set<UUID> spies = new HashSet<>();

    public SpyManager(ChatOS plugin) { this.plugin = plugin; }

    
    public boolean toggleSpy(Player player) {
        UUID uuid = player.getUniqueId();
        if (spies.contains(uuid)) { spies.remove(uuid); return false; }
        spies.add(uuid);
        return true;
    }

    public boolean isSpy(Player player)   { return spies.contains(player.getUniqueId()); }
    public void    removeSpy(Player player) { spies.remove(player.getUniqueId()); }
    public Set<UUID> getSpies()          { return Collections.unmodifiableSet(spies); }
    public int       activeCount()       { return spies.size(); }

    public void handleDisconnect(Player player) { spies.remove(player.getUniqueId()); }
}
