package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public final class StaffBroadcastCommand implements CommandExecutor {

    private final ChatOS plugin;

    public StaffBroadcastCommand(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("chatos.broadcast")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("provide-message")));
            return true;
        }

        String message  = String.join(" ", args);
        String senderName = sender instanceof Player p ? p.getName() : "CONSOLE";

        Component formatted = TextUtil.parse(
            plugin.getConfigUtil().getMessage("broadcast-format")
                .replace("{sender}",  senderName)
                .replace("{message}", message));

        int count = 0;
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (online.hasPermission("chatos.broadcast.receive")) {
                online.sendMessage(formatted);
                if (plugin.getConfigUtil().isBroadcastSoundEnabled()) {
                    try {
                        Sound s = Sound.valueOf(plugin.getConfigUtil().getBroadcastSoundKey());
                        online.playSound(online.getLocation(), s,
                            (float) plugin.getConfigUtil().getBroadcastSoundVolume(),
                            (float) plugin.getConfigUtil().getBroadcastSoundPitch());
                    } catch (IllegalArgumentException ignored) {}
                }
                count++;
            }
        }

        plugin.getLogger().info("[StaffBroadcast] " + senderName + ": " + message + " (" + count + " receiver(s))");
        plugin.getLogManager().logRaw("[StaffBroadcast] " + senderName + ": " + message);
        return true;
    }
}
