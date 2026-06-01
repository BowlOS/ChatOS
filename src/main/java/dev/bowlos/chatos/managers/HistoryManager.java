package dev.bowlos.chatos.managers;

import dev.bowlos.chatos.ChatOS;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public final class HistoryManager {

    public record HistoryEntry(String time, String playerName, String message) {}

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int MAX_PER_CHANNEL = 100;

    private final ChatOS plugin;

    
    private final Map<String, ArrayDeque<HistoryEntry>> history = new HashMap<>();

    public HistoryManager(ChatOS plugin) { this.plugin = plugin; }

    
    public void record(ChatChannel channel, String playerName, String message) {
        ArrayDeque<HistoryEntry> deque =
            history.computeIfAbsent(channel.getId(), k -> new ArrayDeque<>());

        deque.addLast(new HistoryEntry(
            LocalTime.now().format(TIME_FMT),
            playerName,
            message
        ));

        
        while (deque.size() > MAX_PER_CHANNEL) deque.removeFirst();
    }

    
    public List<HistoryEntry> getHistory(ChatChannel channel, int lines) {
        int cap = Math.min(lines, plugin.getConfigUtil().getMaxHistoryLines());
        ArrayDeque<HistoryEntry> deque = history.get(channel.getId());
        if (deque == null || deque.isEmpty()) return List.of();

        List<HistoryEntry> all = new ArrayList<>(deque);
        int fromIndex = Math.max(0, all.size() - cap);
        return all.subList(fromIndex, all.size());
    }

    public int totalFor(ChatChannel channel) {
        ArrayDeque<HistoryEntry> deque = history.get(channel.getId());
        return deque == null ? 0 : deque.size();
    }

    public void clear() { history.clear(); }
}
