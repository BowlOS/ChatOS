package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class HelpOpCommand implements CommandExecutor {

    private final ChatOS plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public HelpOpCommand(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("player-only")));
            return true;
        }
        if (!player.hasPermission("chatos.helpop")) {
            player.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("helpop-no-message")));
            return true;
        }

        
        int cdSeconds = plugin.getConfigUtil().getHelpOpCooldownSeconds();
        if (cdSeconds > 0 && !player.hasPermission("chatos.helpop.cooldown.bypass")) {
            long now     = System.currentTimeMillis();
            long last    = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            long elapsed = (now - last) / 1000L;
            if (elapsed < cdSeconds) {
                player.sendMessage(TextUtil.parse(
                    plugin.getConfigUtil().getMessage("helpop-cooldown")
                        .replace("{seconds}", String.valueOf(cdSeconds - elapsed))));
                return true;
            }
        }
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());

        String message = String.join(" ", args);
        Component formatted = TextUtil.parse(
            plugin.getConfigUtil().getMessage("helpop-format")
                .replace("{displayname}", player.getDisplayName() != null ? player.getDisplayName() : player.getName())
                .replace("{player}",      player.getName())
                .replace("{server}",      plugin.getServer().getName())
                .replace("{message}",     message));

        int count = 0;
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (online.hasPermission("chatos.helpop.receive")) {
                online.sendMessage(formatted);
                if (online.hasPermission("chatos.notify.helpop")) {
                    plugin.getChannelManager().playSound(online, null, true);
                }
                count++;
            }
        }

        plugin.getLogger().info("[HelpOp] " + player.getName() + ": " + message + " (" + count + " receiver(s))");
        plugin.getLogManager().logRaw("[HelpOp] " + player.getName() + "@" + plugin.getServer().getName() + ": " + message);
        player.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("helpop-sent")));
        return true;
    }
}
