package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.managers.ChatChannel;
import dev.bowlos.chatos.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;


public final class ChatOsCommand implements CommandExecutor {

    private final ChatOS plugin;

    public ChatOsCommand(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("chatos.admin")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return true;
        }

        if (args.length == 0) { sendHelp(sender); return true; }

        switch (args[0].toLowerCase()) {
            case "reload"   -> doReload(sender);
            case "info"     -> doInfo(sender);
            case "channels" -> doChannels(sender);
            case "debug"    -> doDebug(sender);
            case "version"  -> doVersion(sender);
            case "help"     -> sendHelp(sender);
            default         -> {
                sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("unknown-command")));
                sendHelp(sender);
            }
        }
        return true;
    }

    

    private void doReload(CommandSender sender) {
        plugin.reload();
        sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("plugin-reloaded")));
    }

    

    private void doInfo(CommandSender sender) {
        Collection<ChatChannel> channels = plugin.getChannelManager().getAllChannels();
        String channelNames = channels.stream()
            .map(ChatChannel::getName).collect(Collectors.joining(", "));

        String proxyStatus = plugin.getConfigUtil().isProxyEnabled()
            ? "&#44ff88Enabled &#8b8b8b(" + plugin.getConfigUtil().getProxyMode() + ")"
            : "&#ff4444Disabled";

        String lpStatus   = plugin.isLuckPermsEnabled()   ? "&#44ff88Linked" : "&#ff4444Not detected";

        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("info-header")
                .replace("{version}", plugin.getDescription().getVersion())));
        sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("info-author")));
        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("info-server")
                .replace("{server}", plugin.getServer().getName())));
        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("info-proxy")
                .replace("{proxy}", proxyStatus)));
        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("info-luckperms")
                .replace("{lp}", lpStatus)));
        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("info-channels")
                .replace("{channels}", channelNames.isEmpty() ? "None" : channelNames)));
        sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("info-footer")));
    }

    

    private void doChannels(CommandSender sender) {
        Collection<ChatChannel> channels = plugin.getChannelManager().getAllChannels();
        sender.sendMessage(TextUtil.parse("&#00b4ff ────── &#ffffffRegistered Channels &#00b4ff──────"));
        if (channels.isEmpty()) {
            sender.sendMessage(TextUtil.parse("  &#8b8b8bNo channels configured."));
        } else {
            for (ChatChannel ch : channels) {
                String muted = ch.isMuted() ? " &#ff4444[MUTED]" : "";
                String proxy = ch.isProxyBroadcast() ? " &#44ff88[PROXY]" : "";
                sender.sendMessage(TextUtil.parse(
                    "  " + ch.getColor() + ch.getName()
                    + " &#8b8b8b(" + ch.getId() + ")"
                    + " &#8b8b8b| " + ch.getPermission()
                    + muted + proxy));
            }
        }
        sender.sendMessage(TextUtil.parse("&#00b4ff ───────────────────────────────"));
    }

    

    private void doDebug(CommandSender sender) {
        if (!sender.hasPermission("chatos.debug")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return;
        }
        sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("debug-header")));
        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("debug-toggles")
                .replace("{count}", String.valueOf(plugin.getToggleManager().activeCount()))));
        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("debug-spies")
                .replace("{count}", String.valueOf(plugin.getSpyManager().activeCount()))));
        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("debug-mutes")
                .replace("{count}", String.valueOf(plugin.getMuteManager().activeCount()))));
        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("debug-staffmode")
                .replace("{count}", String.valueOf(plugin.getStaffModeManager().activeCount()))));
    }

    

    private void doVersion(CommandSender sender) {
        sender.sendMessage(TextUtil.parse(
            "&#00b4ff◈ &#ffffffChatOS &#8b8b8bv" + plugin.getDescription().getVersion()
            + "  &#8b8b8b· Developed by &#ffffffBowlOS"));
        sender.sendMessage(TextUtil.parse(
            "  &#8b8b8bPaper 1.21.1+  ·  BungeeCord  ·  Velocity"));
    }

    

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(TextUtil.parse("&#00b4ff ════ &#ffffffChatOS Admin Help &#00b4ff════"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/chatos reload    &#8b8b8b— Reload config"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/chatos info      &#8b8b8b— Plugin status"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/chatos channels  &#8b8b8b— List channels"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/chatos debug     &#8b8b8b— Runtime stats"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/chatos version   &#8b8b8b— Version info"));
        sender.sendMessage(TextUtil.parse("  &#00b4ff/channel help     &#8b8b8b— Channel management"));
        sender.sendMessage(TextUtil.parse("&#00b4ff ═══════════════════════════"));
    }
}
