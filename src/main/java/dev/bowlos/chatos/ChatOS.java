package dev.bowlos.chatos;

import dev.bowlos.chatos.commands.*;
import dev.bowlos.chatos.listeners.PlayerChatListener;
import dev.bowlos.chatos.listeners.PlayerConnectionListener;
import dev.bowlos.chatos.managers.*;
import dev.bowlos.chatos.messaging.MessagingManager;
import dev.bowlos.chatos.util.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;


public final class ChatOS extends JavaPlugin {

    private static ChatOS instance;

    
    private ConfigUtil        configUtil;
    private ChannelManager    channelManager;
    private ToggleManager     toggleManager;
    private SpyManager        spyManager;
    private MuteManager       muteManager;
    private StaffModeManager  staffModeManager;
    private HistoryManager    historyManager;
    private LogManager        logManager;
    private MessagingManager  messagingManager;

    
    private boolean luckPermsEnabled    = false;

    

    @Override
    public void onEnable() {
        instance = this;

        printBanner();

        saveDefaultConfig();
        this.configUtil = new ConfigUtil(this);

        detectSoftDependencies();

        
        this.channelManager   = new ChannelManager(this);
        this.toggleManager    = new ToggleManager(this);
        this.spyManager       = new SpyManager(this);
        this.muteManager      = new MuteManager(this);
        this.staffModeManager = new StaffModeManager(this);
        this.historyManager   = new HistoryManager(this);
        this.logManager       = new LogManager(this);
        this.messagingManager = new MessagingManager(this);

        registerCommands();
        registerListeners();

        getLogger().info("ChatOS v" + getDescription().getVersion() + " enabled. Developed by BowlOS.");
    }

    @Override
    public void onDisable() {
        if (messagingManager != null) messagingManager.close();
        if (logManager       != null) logManager.close();
        getLogger().info("ChatOS disabled.");
    }

    

    private void detectSoftDependencies() {
        if (getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            luckPermsEnabled = true;
            getLogger().info("[ChatOS] LuckPerms detected — rank prefixes enabled.");
        }
    }

    

    private void registerCommands() {
        
        var sc = getCommand("staffchat");
        if (sc != null) { sc.setExecutor(new StaffChatCommand(this, "staff"));  sc.setTabCompleter(new ChatTabCompleter(this)); }

        var ac = getCommand("adminchat");
        if (ac != null) { ac.setExecutor(new StaffChatCommand(this, "admin"));  ac.setTabCompleter(new ChatTabCompleter(this)); }

        var dc = getCommand("devmode");
        if (dc != null) { dc.setExecutor(new StaffChatCommand(this, "dev"));    dc.setTabCompleter(new ChatTabCompleter(this)); }

        
        var ho = getCommand("helpop");
        if (ho != null) { ho.setExecutor(new HelpOpCommand(this)); ho.setTabCompleter(new ChatTabCompleter(this)); }

        
        var sl = getCommand("stafflist");
        if (sl != null) { sl.setExecutor(new StaffListCommand(this)); }

        
        var spy = getCommand("staffchatspy");
        if (spy != null) { spy.setExecutor(new SpyCommand(this)); spy.setTabCompleter(new SpyTabCompleter(this)); }

        
        var sm = getCommand("staffmode");
        if (sm != null) { sm.setExecutor(new StaffModeCommand(this)); sm.setTabCompleter(new StaffModeTabCompleter(this)); }

        
        var sh = getCommand("staffhistory");
        if (sh != null) { sh.setExecutor(new StaffHistoryCommand(this)); sh.setTabCompleter(new StaffHistoryTabCompleter(this)); }

        
        var sb = getCommand("staffbroadcast");
        if (sb != null) { sb.setExecutor(new StaffBroadcastCommand(this)); sb.setTabCompleter(new ChatTabCompleter(this)); }

        
        var smu = getCommand("staffmute");
        if (smu != null) { smu.setExecutor(new StaffMuteCommand(this, true));  smu.setTabCompleter(new StaffMuteTabCompleter(this)); }

        var suu = getCommand("staffunmute");
        if (suu != null) { suu.setExecutor(new StaffMuteCommand(this, false)); suu.setTabCompleter(new StaffMuteTabCompleter(this)); }

        
        var ch = getCommand("channel");
        if (ch != null) { ch.setExecutor(new ChannelCommand(this)); ch.setTabCompleter(new ChannelTabCompleter(this)); }

        
        var co = getCommand("chatos");
        if (co != null) { co.setExecutor(new ChatOsCommand(this)); co.setTabCompleter(new ChatOsTabCompleter(this)); }
    }

    

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
    }

    

    public void reload() {
        reloadConfig();
        configUtil.reload();
        channelManager.reload();
        messagingManager.reload();
    }

    

    private void printBanner() {
        getLogger().info("  ");
        getLogger().info("  ██████╗██╗  ██╗ █████╗ ████████╗ ██████╗ ███████╗");
        getLogger().info(" ██╔════╝██║  ██║██╔══██╗╚══██╔══╝██╔═══██╗██╔════╝");
        getLogger().info(" ██║     ███████║███████║   ██║   ██║   ██║███████╗ ");
        getLogger().info(" ██║     ██╔══██║██╔══██║   ██║   ██║   ██║╚════██║ ");
        getLogger().info(" ╚██████╗██║  ██║██║  ██║   ██║   ╚██████╔╝███████║ ");
        getLogger().info("  ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝  ╚═╝    ╚═════╝ ╚══════╝ ");
        getLogger().info("  by BowlOS  ·  v" + getDescription().getVersion());
        getLogger().info("  ");
    }

    

    public static ChatOS getInstance() { return instance; }

    

    public ConfigUtil       getConfigUtil()       { return configUtil; }
    public ChannelManager   getChannelManager()   { return channelManager; }
    public ToggleManager    getToggleManager()    { return toggleManager; }
    public SpyManager       getSpyManager()       { return spyManager; }
    public MuteManager      getMuteManager()      { return muteManager; }
    public StaffModeManager getStaffModeManager() { return staffModeManager; }
    public HistoryManager   getHistoryManager()   { return historyManager; }
    public LogManager       getLogManager()       { return logManager; }
    public MessagingManager getMessagingManager() { return messagingManager; }
    public boolean isLuckPermsEnabled()           { return luckPermsEnabled; }
}
