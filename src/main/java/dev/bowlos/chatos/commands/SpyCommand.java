package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class SpyCommand implements CommandExecutor {

    private final ChatOS plugin;

    public SpyCommand(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("player-only")));
            return true;
        }
        if (!player.hasPermission("chatos.spy")) {
            player.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return true;
        }

        if (args.length > 0) {
            boolean desiredState = args[0].equalsIgnoreCase("on");
            boolean currentState = plugin.getSpyManager().isSpy(player);
            if (desiredState == currentState) {
                
                player.sendMessage(TextUtil.parse(plugin.getConfigUtil()
                    .getMessage(desiredState ? "spy-on" : "spy-off")));
                return true;
            }
            plugin.getSpyManager().toggleSpy(player);
            player.sendMessage(TextUtil.parse(plugin.getConfigUtil()
                .getMessage(desiredState ? "spy-on" : "spy-off")));
            return true;
        }

        boolean nowOn = plugin.getSpyManager().toggleSpy(player);
        player.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage(nowOn ? "spy-on" : "spy-off")));
        return true;
    }
}
