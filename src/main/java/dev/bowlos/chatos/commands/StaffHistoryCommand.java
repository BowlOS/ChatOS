package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.managers.ChatChannel;
import dev.bowlos.chatos.managers.HistoryManager;
import dev.bowlos.chatos.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;


public final class StaffHistoryCommand implements CommandExecutor {

    private static final int DEFAULT_LINES = 10;
    private static final int EXTENDED_CAP  = 100;
    private static final int NORMAL_CAP    = 25;

    private final ChatOS plugin;

    public StaffHistoryCommand(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("chatos.history")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return true;
        }

        
        String  channelId = "staff";
        int     lines     = DEFAULT_LINES;

        if (args.length >= 1) {
            
            try {
                lines = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException e) {
                channelId = args[0].toLowerCase();
            }
        }
        if (args.length >= 2) {
            try {
                lines = Math.max(1, Integer.parseInt(args[1]));
            } catch (NumberFormatException ignored) {}
        }

        
        int cap = sender.hasPermission("chatos.history.extended") ? EXTENDED_CAP : NORMAL_CAP;
        lines = Math.min(lines, cap);

        
        Optional<ChatChannel> opt = plugin.getChannelManager().getChannel(channelId);
        if (opt.isEmpty()) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("unknown-channel")
                    .replace("{channel}", channelId)));
            return true;
        }
        ChatChannel channel = opt.get();

        
        List<HistoryManager.HistoryEntry> entries =
            plugin.getHistoryManager().getHistory(channel, lines);
        int total = plugin.getHistoryManager().totalFor(channel);

        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("history-header")
                .replace("{channel}", channel.getName())));

        if (entries.isEmpty()) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("history-empty")));
        } else {
            for (HistoryManager.HistoryEntry e : entries) {
                sender.sendMessage(TextUtil.parse(
                    plugin.getConfigUtil().getMessage("history-entry")
                        .replace("{time}",    e.time())
                        .replace("{player}",  e.playerName())
                        .replace("{message}", e.message())));
            }
        }

        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("history-footer")
                .replace("{count}", String.valueOf(entries.size()))
                .replace("{total}", String.valueOf(total))));

        return true;
    }
}
