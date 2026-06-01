package dev.bowlos.chatos.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class TextUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private static final LegacyComponentSerializer LEGACY =
        LegacyComponentSerializer.builder()
            .character('§')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private TextUtil() {}

    
    public static Component parse(String text) {
        if (text == null) return Component.empty();
        return LEGACY.deserialize(translateHex(translateAmpersand(text)));
    }

    
    public static String translate(String text) {
        if (text == null) return "";
        return translateHex(translateAmpersand(text));
    }

    
    public static String strip(String text) {
        if (text == null) return "";
        return translate(text)
            .replaceAll("§x(§[0-9a-fA-F]){6}", "")
            .replaceAll("§[0-9a-fk-orA-FK-OR]", "");
    }

    

    private static String translateAmpersand(String text) {
        return text.replace('&', '§');
    }

    private static String translateHex(String text) {
        Matcher m = HEX_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            StringBuilder r = new StringBuilder("§x");
            for (char c : m.group(1).toCharArray()) r.append('§').append(c);
            m.appendReplacement(sb, r.toString());
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
