package dev.bowlos.chatos.listeners;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.managers.ChatChannel;
import dev.bowlos.chatos.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public final class PlayerConnectionListener implements Listener {

    private final ChatOS plugin;

    public PlayerConnectionListener(ChatOS plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (plugin.getConfigUtil().isStaffJoinNotify() && isStaff(player)) {
            Component msg = buildMsg("staff-join", player);
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                if (!online.equals(player) && online.hasPermission("chatos.notify.join")) {
                    online.sendMessage(msg);
                }
            }
            plugin.getLogManager().logRaw("[JOIN] " + player.getName() + " joined.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (plugin.getConfigUtil().isStaffJoinNotify() && isStaff(player)) {
            Component msg = buildMsg("staff-quit", player);
            for (Player online : plugin.getServer().getOnlinePlayers()) {
                if (!online.equals(player) && online.hasPermission("chatos.notify.join")) {
                    online.sendMessage(msg);
                }
            }
            plugin.getLogManager().logRaw("[QUIT] " + player.getName() + " left.");
        }

        
        plugin.getToggleManager().handleDisconnect(player);
        plugin.getSpyManager().handleDisconnect(player);
        plugin.getStaffModeManager().handleDisconnect(player);
    }

    

    private Component buildMsg(String key, Player player) {
        return TextUtil.parse(
            plugin.getConfigUtil().getMessage(key)
                .replace("{displayname}", player.getDisplayName() != null
                    ? player.getDisplayName() : player.getName())
                .replace("{player}", player.getName())
                .replace("{server}", plugin.getServer().getName()));
    }

    private boolean isStaff(Player player) {
        for (ChatChannel ch : plugin.getChannelManager().getAllChannels()) {
            if (player.hasPermission(ch.getPermission())) return true;
        }
        return false;
    }
}
