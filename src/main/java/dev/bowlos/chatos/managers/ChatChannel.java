package dev.bowlos.chatos.managers;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;


public final class ChatChannel {

    private final String id;
    private String name;
    private String permission;
    private String format;
    private String prefix;
    private String spyFormat;
    private String color;
    private boolean proxyBroadcast;
    private boolean logged;
    private boolean muted;
    private String soundOverride;
    private final List<String> aliases;
    private final boolean defaultChannel;

    public ChatChannel(String id, ConfigurationSection section) {
        this.id             = id;
        this.name           = section.getString("name", id);
        this.permission     = section.getString("permission", "chatos." + id);
        this.format         = section.getString("format", "&#00b4ff{displayname} &#8b8b8b» &#ffffff{message}");
        this.prefix         = section.getString("prefix", "[" + id.toUpperCase() + "]");
        this.spyFormat      = section.getString("spy-format", "&#8b8b8b[SPY] {displayname} » {message}");
        this.color          = section.getString("color", "&#ffffff");
        this.proxyBroadcast = section.getBoolean("proxy-broadcast", false);
        this.logged         = section.getBoolean("logged", true);
        this.muted          = section.getBoolean("muted", false);
        this.soundOverride  = section.getString("sound-override", "");
        this.aliases        = section.getStringList("aliases");
        this.defaultChannel = section.getBoolean("default", false);
    }

    
    public ChatChannel(String id, String name, String permission, String color) {
        this.id             = id;
        this.name           = name;
        this.permission     = permission;
        this.format         = color + "{displayname} &#8b8b8b» &#ffffff{message}";
        this.prefix         = color + "[" + id.toUpperCase() + "]";
        this.spyFormat      = "&#8b8b8b[SPY " + color + id.toUpperCase() + "&#8b8b8b] {displayname} » {message}";
        this.color          = color;
        this.proxyBroadcast = false;
        this.logged         = true;
        this.muted          = false;
        this.soundOverride  = "";
        this.aliases        = new ArrayList<>();
        this.defaultChannel = false;
    }

    

    public String  getId()             { return id; }
    public String  getName()           { return name; }
    public String  getPermission()     { return permission; }
    public String  getFormat()         { return format; }
    public String  getPrefix()         { return prefix; }
    public String  getSpyFormat()      { return spyFormat; }
    public String  getColor()          { return color; }
    public boolean isProxyBroadcast()  { return proxyBroadcast; }
    public boolean isLogged()          { return logged; }
    public boolean isMuted()           { return muted; }
    public String  getSoundOverride()  { return soundOverride; }
    public List<String> getAliases()   { return aliases; }
    public boolean isDefaultChannel()  { return defaultChannel; }

    

    public void setName(String name)                 { this.name = name; }
    public void setPermission(String permission)     { this.permission = permission; }
    public void setFormat(String format)             { this.format = format; }
    public void setPrefix(String prefix)             { this.prefix = prefix; }
    public void setSpyFormat(String spyFormat)       { this.spyFormat = spyFormat; }
    public void setColor(String color)               { this.color = color; }
    public void setProxyBroadcast(boolean val)       { this.proxyBroadcast = val; }
    public void setLogged(boolean val)               { this.logged = val; }
    public void setMuted(boolean muted)              { this.muted = muted; }
    public void setSoundOverride(String soundOverride) { this.soundOverride = soundOverride; }
}
