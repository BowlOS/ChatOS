package dev.bowlos.chatos.managers;

import dev.bowlos.chatos.ChatOS;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;


public final class LogManager {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final ChatOS plugin;
    private final Path logsDir;
    private PrintWriter writer;
    private String currentDate;

    public LogManager(ChatOS plugin) {
        this.plugin  = plugin;
        this.logsDir = plugin.getDataFolder().toPath().resolve("logs");
        try { Files.createDirectories(logsDir); }
        catch (IOException e) { plugin.getLogger().log(Level.SEVERE, "[ChatOS] Could not create logs dir!", e); }
        openWriter();
    }

    public void log(ChatChannel channel, org.bukkit.entity.Player sender, String message) {
        if (!plugin.getConfigUtil().isLogToFile()) return;
        rotateIfNeeded();
        if (writer == null) return;
        writer.printf("[%s] [%s] %s: %s%n",
            LocalDateTime.now().format(TIME_FMT),
            channel.getName().toUpperCase(),
            sender.getName(),
            message);
        writer.flush();
    }

    public void logRaw(String line) {
        if (!plugin.getConfigUtil().isLogToFile()) return;
        rotateIfNeeded();
        if (writer == null) return;
        writer.printf("[%s] %s%n", LocalDateTime.now().format(TIME_FMT), line);
        writer.flush();
    }

    public void close() {
        if (writer != null) {
            writer.printf("--- ChatOS disabled at %s ---%n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.flush();
            writer.close();
            writer = null;
        }
    }

    private void openWriter() {
        currentDate = LocalDateTime.now().format(DATE_FMT);
        Path logFile = logsDir.resolve("chatos-" + currentDate + ".log");
        try {
            FileOutputStream fos = new FileOutputStream(logFile.toFile(), true);
            writer = new PrintWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.printf("--- ChatOS started at %s ---%n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.flush();
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[ChatOS] Could not open log file!", e);
        }
    }

    private void rotateIfNeeded() {
        String today = LocalDateTime.now().format(DATE_FMT);
        if (!today.equals(currentDate)) { close(); openWriter(); }
    }
}
