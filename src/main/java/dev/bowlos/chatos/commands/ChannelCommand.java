package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.managers.ChatChannel;
import dev.bowlos.chatos.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;


public final class ChannelCommand implements CommandExecutor {

    private final ChatOS plugin;

    public ChannelCommand(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("chatos.channel")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "list"   -> doList(sender);
            case "info"   -> doInfo(sender, args);
            case "create" -> doCreate(sender, args);
            case "delete" -> doDelete(sender, args);
            case "mute"   -> doMute(sender, args);
            case "edit"   -> doEdit(sender, args);
            default       -> sendHelp(sender);
        }

        return true;
    }


    private void doList(CommandSender sender) {
        Collection<ChatChannel> all = plugin.getChannelManager().getAllChannels();
        sender.sendMessage(TextUtil.parse("&#00b4ff ────── &#ffffffChannels &#00b4ff──────"));
        if (all.isEmpty()) {
            sender.sendMessage(TextUtil.parse("  &#8b8b8bNo channels configured."));
        } else {
            for (ChatChannel ch : all) {
                String muted   = ch.isMuted()          ? " &#ff4444[MUTED]"   : "";
                String proxy   = ch.isProxyBroadcast() ? " &#44ff88[PROXY]"   : "";
                String dfault  = ch.isDefaultChannel() ? " &#ffaa00[DEFAULT]" : "";
                sender.sendMessage(TextUtil.parse(
                    "  " + ch.getColor() + ch.getName()
                    + " &#8b8b8b(" + ch.getId() + ")"
                    + muted + proxy + dfault));
            }
        }
        sender.sendMessage(TextUtil.parse("&#00b4ff ──────────────────────"));
    }


    private void doInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(TextUtil.parse("&#ff4444✗ &#ffffffUsage: /channel info <id>"));
            return;
        }
        Optional<ChatChannel> opt = plugin.getChannelManager().getChannel(args[1]);
        if (opt.isEmpty()) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("channel-not-found")
                    .replace("{channel}", args[1])));
            return;
        }
        ChatChannel ch = opt.get();
        sender.sendMessage(TextUtil.parse("&#00b4ff ────── &#ffffff" + ch.getName() + " &#00b4ff──────"));
        infoLine(sender, "ID",          ch.getId());
        infoLine(sender, "Name",        ch.getName());
        infoLine(sender, "Permission",  ch.getPermission());
        infoLine(sender, "Color",       ch.getColor() + ch.getColor());
        infoLine(sender, "Prefix",      ch.getPrefix());
        infoLine(sender, "Format",      ch.getFormat());
        infoLine(sender, "Spy Format",  ch.getSpyFormat());
        infoLine(sender, "Proxy",       String.valueOf(ch.isProxyBroadcast()));
        infoLine(sender, "Logged",      String.valueOf(ch.isLogged()));
        infoLine(sender, "Muted",       String.valueOf(ch.isMuted()));
        infoLine(sender, "Sound",       ch.getSoundOverride().isEmpty() ? "global" : ch.getSoundOverride());
        sender.sendMessage(TextUtil.parse("&#00b4ff ──────────────────────"));
    }

    

    private void doCreate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("chatos.channel.create")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return;
        }
        
        if (args.length < 5) {
            sender.sendMessage(TextUtil.parse(
                "&#ff4444✗ &#ffffffUsage: /channel create <id> <name> <permission> <color>"));
            sender.sendMessage(TextUtil.parse(
                "  &#8b8b8bExample: /channel create mod &#34e4ffModChat &#ffffffchatos.modchat &#34e4ff&#34e4ff"));
            return;
        }

        String id    = args[1].toLowerCase();
        String name  = args[2].replace("_", " ");
        String perm  = args[3];
        String color = args[4];

        if (plugin.getChannelManager().getChannel(id).isPresent()) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("channel-exists")
                    .replace("{channel}", id)));
            return;
        }

        plugin.getChannelManager().createChannel(id, name, perm, color);
        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("channel-created")
                .replace("{channel}", id)));
    }

    

    private void doDelete(CommandSender sender, String[] args) {
        if (!sender.hasPermission("chatos.channel.delete")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(TextUtil.parse("&#ff4444✗ &#ffffffUsage: /channel delete <id>"));
            return;
        }

        Optional<ChatChannel> opt = plugin.getChannelManager().getChannel(args[1]);
        if (opt.isEmpty()) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("channel-not-found")
                    .replace("{channel}", args[1])));
            return;
        }
        if (opt.get().isDefaultChannel()) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("channel-no-delete-default")));
            return;
        }

        boolean deleted = plugin.getChannelManager().deleteChannel(args[1]);
        if (deleted) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("channel-deleted")
                    .replace("{channel}", args[1])));
        }
    }

    

    private void doMute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("chatos.channel.mute")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(TextUtil.parse("&#ff4444✗ &#ffffffUsage: /channel mute <id>"));
            return;
        }
        plugin.getChannelManager().getChannel(args[1]).ifPresentOrElse(ch -> {
            ch.setMuted(!ch.isMuted());
            String key = ch.isMuted() ? "channel-mute-on" : "channel-mute-off";
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage(key)
                    .replace("{name}", ch.getName())));
        }, () -> sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("channel-not-found")
                .replace("{channel}", args[1]))));
    }

    

    private void doEdit(CommandSender sender, String[] args) {
        if (!sender.hasPermission("chatos.channel.edit")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return;
        }
        
        if (args.length < 4) {
            sender.sendMessage(TextUtil.parse(
                "&#ff4444✗ &#ffffffUsage: /channel edit <id> <property> <value>"));
            sender.sendMessage(TextUtil.parse(
                "  &#8b8b8bProperties: name | permission | color | format | spy-format |"));
            sender.sendMessage(TextUtil.parse(
                "  &#8b8b8b            prefix | proxy-broadcast | logged | sound-override"));
            return;
        }

        Optional<ChatChannel> opt = plugin.getChannelManager().getChannel(args[1]);
        if (opt.isEmpty()) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("channel-not-found")
                    .replace("{channel}", args[1])));
            return;
        }

        ChatChannel ch   = opt.get();
        String property  = args[2].toLowerCase();
        String value     = String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length));

        boolean applied = switch (property) {
            case "name"             -> { ch.setName(value);             yield true; }
            case "permission"       -> { ch.setPermission(value);       yield true; }
            case "color"            -> { ch.setColor(value);            yield true; }
            case "format"           -> { ch.setFormat(value);           yield true; }
            case "spy-format"       -> { ch.setSpyFormat(value);        yield true; }
            case "prefix"           -> { ch.setPrefix(value);           yield true; }
            case "sound-override"   -> { ch.setSoundOverride(value);    yield true; }
            case "proxy-broadcast"  -> { ch.setProxyBroadcast(Boolean.parseBoolean(value)); yield true; }
            case "logged"           -> { ch.setLogged(Boolean.parseBoolean(value));        yield true; }
            default -> false;
        };

        if (applied) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("channel-edited")
                    .replace("{channel}",  ch.getName())
                    .replace("{property}", property)
                    .replace("{value}",    value)));
        } else {
            sender.sendMessage(TextUtil.parse(
                "&#ff4444✗ &#ffffffUnknown property: &#ffffff" + property));
        }
    }

    

    private void infoLine(CommandSender sender, String label, String value) {
        String padded = String.format("%-14s", label);
        sender.sendMessage(TextUtil.parse(
            "  &#8b8b8b" + padded + "&#ffffff" + value));
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(TextUtil.parse("&#00b4ff ────── &#ffffff/channel Help &#00b4ff──────"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/channel list                     &#8b8b8bList all channels"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/channel info <id>                &#8b8b8bChannel details"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/channel create <id> <n> <p> <c>  &#8b8b8bCreate new channel"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/channel delete <id>              &#8b8b8bDelete a channel"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/channel mute <id>               &#8b8b8bToggle mute"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/channel edit <id> <prop> <val>  &#8b8b8bEdit a property"));
        sender.sendMessage(TextUtil.parse("&#00b4ff ──────────────────────────────────"));
    }
}
