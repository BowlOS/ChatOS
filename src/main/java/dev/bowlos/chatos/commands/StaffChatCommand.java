package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.managers.ChatChannel;
import dev.bowlos.chatos.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public final class StaffChatCommand implements CommandExecutor {

    private final ChatOS plugin;
    private final String channelId;

    public StaffChatCommand(ChatOS plugin, String channelId) {
        this.plugin    = plugin;
        this.channelId = channelId;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("player-only")));
            return true;
        }

        Optional<ChatChannel> opt = plugin.getChannelManager().getChannel(channelId);
        if (opt.isEmpty()) {
            player.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("unknown-channel")
                    .replace("{channel}", channelId)));
            return true;
        }
        ChatChannel channel = opt.get();

        if (!player.hasPermission(channel.getPermission())) {
            player.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return true;
        }

        if (args.length > 0) {
            
            String message = String.join(" ", args);
            plugin.getChannelManager().dispatch(channel, player, message);
            player.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("quick-send")
                    .replace("{color}", channel.getColor())
                    .replace("{name}", channel.getName())));
            return true;
        }

        
        boolean nowOn = plugin.getToggleManager().toggle(player, channel);
        String msgKey = nowOn ? "toggle-on" : "toggle-off";
        player.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage(msgKey)
                .replace("{color}", channel.getColor())
                .replace("{name}", channel.getName())));
        return true;
    }
}
