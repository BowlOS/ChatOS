package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public final class SpyTabCompleter implements TabCompleter {

    private final ChatOS plugin;

    public SpyTabCompleter(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (!sender.hasPermission("chatos.spy")) return List.of();

        if (args.length == 1) {
            return List.of("on", "off").stream()
                .filter(s -> s.startsWith(args[0].toLowerCase()))
                .toList();
        }
        return List.of();
    }
}
