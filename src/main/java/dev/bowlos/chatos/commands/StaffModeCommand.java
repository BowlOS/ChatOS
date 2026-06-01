package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public final class StaffModeCommand implements CommandExecutor {

    private final ChatOS plugin;

    public StaffModeCommand(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("chatos.staffmode")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return true;
        }

        
        Player target;
        if (args.length > 0 && sender.hasPermission("chatos.staffmode.others")) {
            target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(TextUtil.parse(
                    plugin.getConfigUtil().getMessage("invalid-player")
                        .replace("{player}", args[0])));
                return true;
            }
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("player-only")));
                return true;
            }
            target = player;
        }

        boolean nowOn = plugin.getStaffModeManager().toggle(target);
        boolean self  = sender instanceof Player p && p.equals(target);

        if (self) {
            target.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage(nowOn ? "staffmode-on" : "staffmode-off")));
        } else {
            target.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage(nowOn ? "staffmode-on" : "staffmode-off")));
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage(nowOn ? "staffmode-on-other" : "staffmode-off-other")
                    .replace("{player}", target.getName())));
        }

        plugin.getLogManager().logRaw("[StaffMode] " + (self ? target.getName() : sender.getName() + " → " + target.getName())
            + " | " + (nowOn ? "ENABLED" : "DISABLED"));
        return true;
    }
}
