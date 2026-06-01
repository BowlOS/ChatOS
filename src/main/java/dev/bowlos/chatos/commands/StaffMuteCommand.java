package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public final class StaffMuteCommand implements CommandExecutor {

    private final ChatOS plugin;
    private final boolean isMute; 

    public StaffMuteCommand(ChatOS plugin, boolean isMute) {
        this.plugin  = plugin;
        this.isMute  = isMute;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("chatos.staffmute")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(TextUtil.parse(
                isMute ? "&#ff4444✗ &#ffffffUsage: /staffmute <player> [reason]"
                       : "&#ff4444✗ &#ffffffUsage: /staffunmute <player>"));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("invalid-player")
                    .replace("{player}", args[0])));
            return true;
        }

        String staffName = sender instanceof Player p ? p.getName() : "Console";

        if (isMute) {
            if (plugin.getMuteManager().isMuted(target)) {
                sender.sendMessage(TextUtil.parse(
                    plugin.getConfigUtil().getMessage("staff-already-muted")
                        .replace("{player}", target.getName())));
                return true;
            }

            String reason = args.length > 1
                ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length))
                : "No reason provided.";

            plugin.getMuteManager().mute(target, reason);

            
            target.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("staff-muted-notify")
                    .replace("{staff}", staffName)));

            
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("staff-muted-other")
                    .replace("{player}", target.getName())));

            plugin.getLogManager().logRaw(
                "[StaffMute] " + staffName + " muted " + target.getName() + " — Reason: " + reason);

        } else {
            
            if (!plugin.getMuteManager().isMuted(target)) {
                sender.sendMessage(TextUtil.parse(
                    plugin.getConfigUtil().getMessage("staff-not-muted")
                        .replace("{player}", target.getName())));
                return true;
            }

            plugin.getMuteManager().unmute(target);

            target.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("staff-unmuted-notify")
                    .replace("{staff}", staffName)));

            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("staff-unmuted-other")
                    .replace("{player}", target.getName())));

            plugin.getLogManager().logRaw(
                "[StaffUnmute] " + staffName + " unmuted " + target.getName());
        }

        return true;
    }
}
