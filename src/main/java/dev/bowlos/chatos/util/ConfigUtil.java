package dev.bowlos.chatos.util;

import dev.bowlos.chatos.ChatOS;


public final class ConfigUtil {

    private final ChatOS plugin;

    public ConfigUtil(ChatOS plugin) { this.plugin = plugin; }

    public void reload() {  }

    

    public String getPluginPrefix() {
        return plugin.getConfig().getString("settings.plugin-prefix", "&#00b4ff◈ &#ffffff ChatOS &#8b8b8b│");
    }

    public boolean isLogToConsole()    { return plugin.getConfig().getBoolean("settings.log-to-console", true); }
    public boolean isLogToFile()       { return plugin.getConfig().getBoolean("settings.log-to-file", true); }
    public boolean isStaffJoinNotify() { return plugin.getConfig().getBoolean("settings.staff-join-notify", true); }
    public int getAutoToggleOffSeconds() { return plugin.getConfig().getInt("settings.auto-toggle-off-seconds", 0); }
    public int getMaxHistoryLines()    { return plugin.getConfig().getInt("settings.max-history-lines", 50); }

    

    public boolean isWordFilterEnabled() {
        return plugin.getConfig().getBoolean("settings.word-filter.enabled", false);
    }

    public java.util.List<String> getFilteredWords() {
        return plugin.getConfig().getStringList("settings.word-filter.words");
    }

    

    public boolean isSoundEnabled()      { return plugin.getConfig().getBoolean("settings.message-sound.enabled", true); }
    public String  getSoundKey()         { return plugin.getConfig().getString("settings.message-sound.sound", "ENTITY_EXPERIENCE_ORB_PICKUP"); }
    public double  getSoundVolume()      { return plugin.getConfig().getDouble("settings.message-sound.volume", 0.8); }
    public double  getSoundPitch()       { return plugin.getConfig().getDouble("settings.message-sound.pitch", 1.4); }

    public boolean isHelpOpSoundEnabled() { return plugin.getConfig().getBoolean("settings.helpop-sound.enabled", true); }
    public String  getHelpOpSoundKey()    { return plugin.getConfig().getString("settings.helpop-sound.sound", "BLOCK_NOTE_BLOCK_PLING"); }
    public double  getHelpOpSoundVolume() { return plugin.getConfig().getDouble("settings.helpop-sound.volume", 1.0); }
    public double  getHelpOpSoundPitch()  { return plugin.getConfig().getDouble("settings.helpop-sound.pitch", 2.0); }

    public boolean isBroadcastSoundEnabled() { return plugin.getConfig().getBoolean("settings.broadcast-sound.enabled", true); }
    public String  getBroadcastSoundKey()    { return plugin.getConfig().getString("settings.broadcast-sound.sound", "ENTITY_PLAYER_LEVELUP"); }
    public double  getBroadcastSoundVolume() { return plugin.getConfig().getDouble("settings.broadcast-sound.volume", 0.6); }
    public double  getBroadcastSoundPitch()  { return plugin.getConfig().getDouble("settings.broadcast-sound.pitch", 1.0); }

    

    public boolean isProxyEnabled() { return plugin.getConfig().getBoolean("settings.proxy.enabled", false); }
    public String  getProxyMode()   { return plugin.getConfig().getString("settings.proxy.mode", "PLUGIN_MESSAGING"); }

    

    public boolean isStaffModeVanish()    { return plugin.getConfig().getBoolean("settings.staff-mode.vanish", true); }
    public boolean isStaffModeGod()       { return plugin.getConfig().getBoolean("settings.staff-mode.god-mode", true); }
    public boolean isStaffModeInvSave()   { return plugin.getConfig().getBoolean("settings.staff-mode.inventory-save", true); }

    

    public String getMessage(String key) {
        return plugin.getConfig().getString("messages." + key, "&#ff4444Missing message: " + key);
    }

    public int getHelpOpCooldownSeconds() {
        return plugin.getConfig().getInt("messages.helpop-cooldown-seconds", 10);
    }
}
