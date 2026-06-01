package dev.bowlos.chatos.commands;

import dev.bowlos.chatos.ChatOS;
import dev.bowlos.chatos.managers.ChatChannel;
import dev.bowlos.chatos.util.TextUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public final class StaffListCommand implements CommandExecutor {

    private final ChatOS plugin;

    public StaffListCommand(ChatOS plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("chatos.stafflist")) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("no-permission")));
            return true;
        }

        
        ChatChannel filterChannel = null;
        if (args.length > 0 && sender.hasPermission("chatos.stafflist.others")) {
            Optional<ChatChannel> opt = plugin.getChannelManager().getChannel(args[0]);
            if (opt.isPresent()) filterChannel = opt.get();
        }

        sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("stafflist-header")));

        List<Player> staffOnline = new ArrayList<>();
        for (Player online : plugin.getServer().getOnlinePlayers()) {
            boolean isStaff = false;
            for (ChatChannel ch : plugin.getChannelManager().getAllChannels()) {
                if (online.hasPermission(ch.getPermission())) { isStaff = true; break; }
            }
            if (!isStaff) continue;
            if (filterChannel != null && !online.hasPermission(filterChannel.getPermission())) continue;
            staffOnline.add(online);
        }

        if (staffOnline.isEmpty()) {
            sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("stafflist-no-staff")));
        } else {
            for (Player staff : staffOnline) {
                
                String channelName  = "None";
                String channelColor = "&#8b8b8b";

                Optional<ChatChannel> toggled = plugin.getToggleManager().getToggledChannel(staff);
                if (toggled.isPresent()) {
                    channelName  = toggled.get().getName();
                    channelColor = toggled.get().getColor();
                } else {
                    for (ChatChannel ch : plugin.getChannelManager().getAllChannels()) {
                        if (staff.hasPermission(ch.getPermission())) {
                            channelName  = ch.getName();
                            channelColor = ch.getColor();
                            break;
                        }
                    }
                }

                
                StringBuilder extras = new StringBuilder();
                if (plugin.getSpyManager().isSpy(staff)
                        && !staff.hasPermission("chatos.spy.stealth")) {
                    extras.append(plugin.getConfigUtil().getMessage("stafflist-spy-tag"));
                }
                if (plugin.getStaffModeManager().isInStaffMode(staff)) {
                    extras.append(plugin.getConfigUtil().getMessage("stafflist-mode-tag"));
                }

                String entry = plugin.getConfigUtil().getMessage("stafflist-entry")
                    .replace("{color}",   channelColor)
                    .replace("{channel}", channelName)
                    .replace("{player}",  staff.getName())
                    .replace("{server}",  plugin.getServer().getName())
                    + extras;

                sender.sendMessage(TextUtil.parse(entry));
            }
        }

        sender.sendMessage(TextUtil.parse(
            plugin.getConfigUtil().getMessage("stafflist-count")
                .replace("{count}",   String.valueOf(staffOnline.size()))
                .replace("{servers}", "1")));

        sender.sendMessage(TextUtil.parse(plugin.getConfigUtil().getMessage("stafflist-footer")));
        return true;
    }
}
