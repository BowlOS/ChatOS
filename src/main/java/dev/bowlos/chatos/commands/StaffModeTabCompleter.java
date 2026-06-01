package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public final class StaffModeTabCompleter implements TabCompleter {

    private final ChatOS plugin;

    public StaffModeTabCompleter(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (!sender.hasPermission("chatos.staffmode.others")) return List.of();

        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    names.add(p.getName());
            }
            return names;
        }
        return List.of();
    }
}
