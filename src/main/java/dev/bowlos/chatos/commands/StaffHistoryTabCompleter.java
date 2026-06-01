package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.managers.ChatChannel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public final class StaffHistoryTabCompleter implements TabCompleter {

    private final ChatOS plugin;

    public StaffHistoryTabCompleter(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (!sender.hasPermission("chatos.history")) return List.of();

        if (args.length == 1) {
            List<String> ids = new ArrayList<>();
            for (ChatChannel ch : plugin.getChannelManager().getAllChannels()) {
                if (ch.getId().startsWith(args[0].toLowerCase())) ids.add(ch.getId());
            }
            return ids;
        }

        if (args.length == 2) {
            return List.of("10", "25", "50");
        }

        return List.of();
    }
}
