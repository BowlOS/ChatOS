package dev.bowlos.chatos.managers;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;


public final class ChannelManager {

    private final ChatOS plugin;
    
    private final LinkedHashMap<String, ChatChannel> channels = new LinkedHashMap<>();

    public ChannelManager(ChatOS plugin) {
        this.plugin = plugin;
        load();
    }

    

    private void load() {
        channels.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("channels");
        if (section == null) {
            plugin.getLogger().warning("[ChannelManager] No channels defined in config.yml!");
            return;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection cs = section.getConfigurationSection(key);
            if (cs != null) {
                channels.put(key.toLowerCase(), new ChatChannel(key.toLowerCase(), cs));
                plugin.getLogger().info("[ChatOS] Loaded channel: " + key);
            }
        }
    }

    public void reload() { load(); }

    

    public Optional<ChatChannel> getChannel(String id) {
        if (id == null) return Optional.empty();
        ChatChannel direct = channels.get(id.toLowerCase());
        if (direct != null) return Optional.of(direct);

        
        for (ChatChannel ch : channels.values()) {
            if (ch.getAliases().contains(id.toLowerCase())) return Optional.of(ch);
        }
        return Optional.empty();
    }

    public Collection<ChatChannel> getAllChannels() {
        return Collections.unmodifiableCollection(channels.values());
    }

    public List<ChatChannel> getAccessibleChannels(Player player) {
        List<ChatChannel> list = new ArrayList<>();
        for (ChatChannel ch : channels.values()) {
            if (player.hasPermission(ch.getPermission())) list.add(ch);
        }
        return list;
    }

    

    public boolean createChannel(String id, String name, String permission, String color) {
        if (channels.containsKey(id.toLowerCase())) return false;
        channels.put(id.toLowerCase(), new ChatChannel(id.toLowerCase(), name, permission, color));
        return true;
    }

    public boolean deleteChannel(String id) {
        ChatChannel ch = channels.get(id.toLowerCase());
        if (ch == null || ch.isDefaultChannel()) return false;
        channels.remove(id.toLowerCase());
        return true;
    }

    

    
    public void dispatch(ChatChannel channel, Player sender, String rawMessage) {

        
        if (plugin.getMuteManager().isMuted(sender)
                && !sender.hasPermission("chatos.staffmute.bypass")) {
            String reason = plugin.getMuteManager().getMuteReason(sender);
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("staff-muted-reason")
                    .replace("{reason}", reason)));
            return;
        }

        
        if (channel.isMuted() && !sender.hasPermission("chatos.bypass.mute")) {
            sender.sendMessage(TextUtil.parse(
                plugin.getConfigUtil().getMessage("channel-muted")
                    .replace("{name}", channel.getName())));
            return;
        }

        
        String filteredMessage = applyFilter(sender, rawMessage);

        String formatted    = buildFormat(channel.getFormat(), channel, sender, filteredMessage);
        String spyFormatted = buildFormat(channel.getSpyFormat(), channel, sender, filteredMessage);

        Component msg    = TextUtil.parse(formatted);
        Component spyMsg = TextUtil.parse(spyFormatted);

        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (online.hasPermission(channel.getPermission())) {
                online.sendMessage(msg);
                playSound(online, channel, false);
            } else if (plugin.getSpyManager().isSpy(online)) {
                online.sendMessage(spyMsg);
                playSound(online, channel, false);
            }
        }

        
        plugin.getHistoryManager().record(channel, sender.getName(), filteredMessage);

        
        if (plugin.getConfigUtil().isLogToConsole()) {
            plugin.getLogger().info("[" + channel.getName() + "] " + sender.getName() + ": " + filteredMessage);
        }

        
        if (channel.isLogged()) {
            plugin.getLogManager().log(channel, sender, filteredMessage);
        }

        
        if (channel.isProxyBroadcast() && plugin.getConfigUtil().isProxyEnabled()) {
            plugin.getMessagingManager().broadcastToProxy(channel, sender, filteredMessage);
        }
    }

    
    public void dispatchFromProxy(String channelId, String senderName,
                                   String displayName, String serverName, String rawMessage) {
        Optional<ChatChannel> opt = getChannel(channelId);
        if (opt.isEmpty()) return;
        ChatChannel channel = opt.get();

        Component msg    = TextUtil.parse(buildProxyFormat(channel.getFormat(), channel, senderName, displayName, serverName, rawMessage));
        Component spyMsg = TextUtil.parse(buildProxyFormat(channel.getSpyFormat(), channel, senderName, displayName, serverName, rawMessage));

        for (Player online : plugin.getServer().getOnlinePlayers()) {
            if (online.hasPermission(channel.getPermission())) {
                online.sendMessage(msg);
                playSound(online, channel, false);
            } else if (plugin.getSpyManager().isSpy(online)) {
                online.sendMessage(spyMsg);
                playSound(online, channel, false);
            }
        }

        if (plugin.getConfigUtil().isLogToConsole()) {
            plugin.getLogger().info("[PROXY][" + channel.getName() + "] "
                + senderName + "@" + serverName + ": " + rawMessage);
        }
    }

    

    private String buildFormat(String template, ChatChannel ch, Player sender, String message) {
        String rank = getRank(sender);
        String result = template
            .replace("{prefix}",      ch.getPrefix())
            .replace("{player}",      sender.getName())
            .replace("{displayname}", sender.getDisplayName() != null ? sender.getDisplayName() : sender.getName())
            .replace("{server}",      plugin.getServer().getName())
            .replace("{message}",     message)
            .replace("{rank}",        rank)
            .replace("{color}",       ch.getColor());
        return result;
    }

    private String buildProxyFormat(String template, ChatChannel ch,
                                    String senderName, String displayName,
                                    String serverName, String message) {
        return template
            .replace("{prefix}",      ch.getPrefix())
            .replace("{player}",      senderName)
            .replace("{displayname}", displayName)
            .replace("{server}",      serverName)
            .replace("{message}",     message)
            .replace("{rank}",        "")
            .replace("{color}",       ch.getColor());
    }

    

    private String getRank(Player player) {
        if (!plugin.isLuckPermsEnabled()) return "";
        try {
            net.luckperms.api.LuckPerms lp = net.luckperms.api.LuckPermsProvider.get();
            net.luckperms.api.model.user.User user = lp.getUserManager().getUser(player.getUniqueId());
            if (user == null) return "";
            String prefix = user.getCachedData().getMetaData().getPrefix();
            return prefix != null ? prefix : "";
        } catch (Exception e) { return ""; }
    }

    private String applyFilter(Player sender, String message) {
        if (!plugin.getConfigUtil().isWordFilterEnabled()) return message;
        if (sender.hasPermission("chatos.bypass.filter")) return message;
        String filtered = message;
        for (String word : plugin.getConfigUtil().getFilteredWords()) {
            String replacement = "*".repeat(word.length());
            filtered = filtered.replaceAll("(?i)" + java.util.regex.Pattern.quote(word), replacement);
        }
        return filtered;
    }

    public void playSound(Player player, ChatChannel channel, boolean isHelpOp) {
        try {
            String key;
            double vol, pitch;
            if (isHelpOp && plugin.getConfigUtil().isHelpOpSoundEnabled()) {
                key   = plugin.getConfigUtil().getHelpOpSoundKey();
                vol   = plugin.getConfigUtil().getHelpOpSoundVolume();
                pitch = plugin.getConfigUtil().getHelpOpSoundPitch();
            } else {
                String override = channel != null ? channel.getSoundOverride() : "";
                if (override != null && !override.isEmpty()) {
                    key   = override;
                    vol   = plugin.getConfigUtil().getSoundVolume();
                    pitch = plugin.getConfigUtil().getSoundPitch();
                } else if (plugin.getConfigUtil().isSoundEnabled()) {
                    key   = plugin.getConfigUtil().getSoundKey();
                    vol   = plugin.getConfigUtil().getSoundVolume();
                    pitch = plugin.getConfigUtil().getSoundPitch();
                } else return;
            }
            Sound sound = Sound.valueOf(key.toUpperCase());
            player.playSound(player.getLocation(), sound, (float) vol, (float) pitch);
        } catch (IllegalArgumentException ignored) {}
    }
}
