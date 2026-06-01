package dev.bowlos.chatos.managers;

import dev.bowlos.chatos.ChatOS;
import org.bukkit.entity.Player;

import java.util.*;


public final class ToggleManager {

    private final ChatOS plugin;
    
    private final Map<UUID, String> toggledPlayers = new HashMap<>();

    public ToggleManager(ChatOS plugin) { this.plugin = plugin; }

    
    public boolean toggle(Player player, ChatChannel channel) {
        UUID uuid = player.getUniqueId();
        if (isToggled(player, channel)) {
            toggledPlayers.remove(uuid);
            return false;
        }
        toggledPlayers.put(uuid, channel.getId());
        return true;
    }

    public void setChannel(Player player, ChatChannel channel) {
        toggledPlayers.put(player.getUniqueId(), channel.getId());
    }

    public void clearToggle(Player player) {
        toggledPlayers.remove(player.getUniqueId());
    }

    public boolean isToggled(Player player, ChatChannel channel) {
        return channel.getId().equals(toggledPlayers.get(player.getUniqueId()));
    }

    public boolean isToggled(Player player) {
        return toggledPlayers.containsKey(player.getUniqueId());
    }

    public Optional<ChatChannel> getToggledChannel(Player player) {
        String id = toggledPlayers.get(player.getUniqueId());
        if (id == null) return Optional.empty();
        return plugin.getChannelManager().getChannel(id);
    }

    public List<Player> getToggledPlayersIn(ChatChannel channel) {
        List<Player> result = new ArrayList<>();
        for (Map.Entry<UUID, String> e : toggledPlayers.entrySet()) {
            if (e.getValue().equals(channel.getId())) {
                Player p = plugin.getServer().getPlayer(e.getKey());
                if (p != null) result.add(p);
            }
        }
        return result;
    }

    public int activeCount() { return toggledPlayers.size(); }

    public void handleDisconnect(Player player) {
        toggledPlayers.remove(player.getUniqueId());
    }
}
