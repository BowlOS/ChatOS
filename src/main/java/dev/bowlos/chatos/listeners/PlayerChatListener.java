package dev.bowlos.chatos.listeners;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.managers.ChatChannel;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Optional;


public final class PlayerChatListener implements Listener {

    private final ChatOS plugin;

    public PlayerChatListener(ChatOS plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        Optional<ChatChannel> toggled = plugin.getToggleManager().getToggledChannel(player);
        if (toggled.isEmpty()) return;

        ChatChannel channel = toggled.get();

        
        String rawMessage = PlainTextComponentSerializer.plainText()
            .serialize(event.message());

        
        event.setCancelled(true);

        
        plugin.getServer().getScheduler().runTask(plugin,
            () -> plugin.getChannelManager().dispatch(channel, player, rawMessage));
    }
}
