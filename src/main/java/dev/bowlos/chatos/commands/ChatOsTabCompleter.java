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


public final class ChatOsTabCompleter implements TabCompleter {

    private static final List<String> SUBS = List.of("reload", "info", "channels", "debug", "version", "help");

    private final ChatOS plugin;

    public ChatOsTabCompleter(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (!sender.hasPermission("chatos.admin")) return List.of();

        if (args.length == 1) {
            return filter(SUBS, args[0]);
        }
        return List.of();
    }

    private List<String> filter(List<String> list, String prefix) {
        List<String> out = new ArrayList<>();
        for (String s : list) if (s.startsWith(prefix.toLowerCase())) out.add(s);
        return out;
    }
}
