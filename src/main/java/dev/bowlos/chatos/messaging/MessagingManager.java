package dev.bowlos.chatos.messaging;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.managers.ChatChannel;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.Collection;
import java.util.logging.Level;


public final class MessagingManager implements PluginMessageListener {

    private static final String BUNGEE_CHANNEL = "BungeeCord";
    private static final String SUB_CHANNEL    = "ChatOS";

    private final ChatOS plugin;
    private boolean registered = false;

    public MessagingManager(ChatOS plugin) {
        this.plugin = plugin;
        if (plugin.getConfigUtil().isProxyEnabled()) register();
    }

    private void register() {
        try {
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, BUNGEE_CHANNEL);
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, BUNGEE_CHANNEL, this);
            registered = true;
            plugin.getLogger().info("[ChatOS] Proxy messaging registered (BungeeCord / Velocity compat).");
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "[ChatOS] Failed to register proxy channel!", e);
        }
    }

    private void unregister() {
        if (!registered) return;
        try {
            plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, BUNGEE_CHANNEL);
            plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, BUNGEE_CHANNEL);
        } catch (Exception ignored) {}
        registered = false;
    }

    public void reload() { unregister(); if (plugin.getConfigUtil().isProxyEnabled()) register(); }
    public void close()  { unregister(); }

    

    public void broadcastToProxy(ChatChannel channel, Player sender, String rawMessage) {
        if (!registered) return;
        Collection<? extends Player> online = plugin.getServer().getOnlinePlayers();
        if (online.isEmpty()) return;
        Player carrier = online.iterator().next();

        try {
            ByteArrayOutputStream payloadBytes = new ByteArrayOutputStream();
            DataOutputStream payloadOut = new DataOutputStream(payloadBytes);
            payloadOut.writeUTF(SUB_CHANNEL);
            payloadOut.writeUTF(channel.getId());
            payloadOut.writeUTF(sender.getName());
            payloadOut.writeUTF(sender.getDisplayName() != null ? sender.getDisplayName() : sender.getName());
            payloadOut.writeUTF(plugin.getServer().getName());
            payloadOut.writeUTF(rawMessage);
            byte[] payload = payloadBytes.toByteArray();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream dataOut = new DataOutputStream(out);
            dataOut.writeUTF("Forward");
            dataOut.writeUTF("ALL");
            dataOut.writeUTF(SUB_CHANNEL);
            dataOut.writeShort(payload.length);
            dataOut.write(payload);

            carrier.sendPluginMessage(plugin, BUNGEE_CHANNEL, out.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[ChatOS] Failed to send proxy message!", e);
        }
    }

    

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(BUNGEE_CHANNEL)) return;
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            if (!in.readUTF().equals(SUB_CHANNEL)) return;
            String channelId    = in.readUTF();
            String senderName   = in.readUTF();
            String displayName  = in.readUTF();
            String serverName   = in.readUTF();
            String rawMessage   = in.readUTF();
            plugin.getChannelManager().dispatchFromProxy(channelId, senderName, displayName, serverName, rawMessage);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[ChatOS] Malformed proxy message!", e);
        }
    }
}
