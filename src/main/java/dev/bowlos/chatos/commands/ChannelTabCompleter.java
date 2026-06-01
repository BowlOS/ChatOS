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


public final class ChannelTabCompleter implements TabCompleter {

    private static final List<String> SUBS = List.of("list", "info", "create", "delete", "mute", "edit");
    private static final List<String> EDIT_PROPS = List.of(
        "name", "permission", "color", "format", "spy-format",
        "prefix", "proxy-broadcast", "logged", "sound-override"
    );

    private final ChatOS plugin;

    public ChannelTabCompleter(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,
                                                @NotNull Command command,
                                                @NotNull String alias,
                                                @NotNull String[] args) {

        if (!sender.hasPermission("chatos.channel")) return List.of();

        if (args.length == 1) return filter(SUBS, args[0]);

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (List.of("info", "delete", "mute", "edit").contains(sub)) {
                return channelIds(args[1]);
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {
            return filter(EDIT_PROPS, args[2]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("edit")) {
            String prop = args[2].toLowerCase();
            if (prop.equals("proxy-broadcast") || prop.equals("logged")) {
                return filter(List.of("true", "false"), args[3]);
            }
        }

        return List.of();
    }

    private List<String> channelIds(String prefix) {
        List<String> out = new ArrayList<>();
        for (ChatChannel ch : plugin.getChannelManager().getAllChannels()) {
            if (ch.getId().startsWith(prefix.toLowerCase())) out.add(ch.getId());
        }
        return out;
    }

    private List<String> filter(List<String> list, String prefix) {
        List<String> out = new ArrayList<>();
        for (String s : list) if (s.startsWith(prefix.toLowerCase())) out.add(s);
        return out;
    }
}
