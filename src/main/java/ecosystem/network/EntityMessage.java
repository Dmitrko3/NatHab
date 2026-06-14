package ecosystem.network;

import java.util.*;

/**
 * Simple parser/serializer for the text protocol:
 * VERSION|ACTION|TYPE|key1=val1,key2=val2
 *
 * Example: 1|SPAWN|Lion|energy=80,x=10,y=15
 */
public final class EntityMessage {

    public enum Action { SPAWN, MOVE, UPDATE, REMOVE, PING, UNKNOWN }

    private final int version;
    private final Action action;
    private final String type;
    private final Map<String,String> payload;

    public EntityMessage(int version, Action action, String type, Map<String,String> payload) {
        this.version = version;
        this.action = action;
        this.type = type;
        this.payload = Collections.unmodifiableMap(new LinkedHashMap<>(payload));
    }

    public int getVersion() { return version; }
    public Action getAction() { return action; }
    public String getType() { return type; }
    public Map<String,String> getPayload() { return payload; }

    // Serialize back to protocol text
    public String toProtocolString() {
        StringBuilder sb = new StringBuilder();
        sb.append(version).append('|');
        sb.append(action.name()).append('|');
        sb.append(type == null ? "" : type).append('|');

        boolean first = true;
        for (Map.Entry<String,String> e : payload.entrySet()) {
            if (!first) sb.append(',');
            sb.append(escape(e.getKey())).append('=').append(escape(e.getValue()));
            first = false;
        }
        return sb.toString();
    }

    // Basic escaping for '=' ',' '|' and '\' characters
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("=", "\\=")
                .replace(",", "\\,")
                .replace("|", "\\|");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder();
        boolean slash = false;
        for (int i=0;i<s.length();i++) {
            char c = s.charAt(i);
            if (slash) {
                out.append(c);
                slash = false;
            } else if (c == '\\') {
                slash = true;
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    // Parse protocol string. Throws IllegalArgumentException on malformed input.
    public static EntityMessage parse(String line) {
        if (line == null) throw new IllegalArgumentException("null line");
        // split into 4 segments at most: version | action | type | payload
        String[] parts = line.split("\\|", 4);
        if (parts.length < 3) throw new IllegalArgumentException("Invalid message, expected at least 3 fields: " + line);

        int version;
        try {
            version = Integer.parseInt(parts[0]);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid version: " + parts[0]);
        }

        Action action;
        try {
            action = Action.valueOf(parts[1]);
        } catch (IllegalArgumentException iae) {
            action = Action.UNKNOWN;
        }

        String type = parts[2];

        Map<String,String> payload = new LinkedHashMap<>();
        if (parts.length == 4 && !parts[3].isEmpty()) {
            String rawPayload = parts[3];
            // split by commas but respect escaping
            List<String> kvPairs = splitRespectingEscape(rawPayload, ',');
            for (String kv : kvPairs) {
                List<String> pair = splitRespectingEscape(kv, '=');
                if (pair.size() != 2) {
                    // allow keys without value (treat as empty string)
                    String key = unescape(pair.get(0));
                    payload.put(key, "");
                } else {
                    String key = unescape(pair.get(0));
                    String val = unescape(pair.get(1));
                    payload.put(key, val);
                }
            }
        }

        return new EntityMessage(version, action, type, payload);
    }

    private static List<String> splitRespectingEscape(String s, char delimiter) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean slash = false;
        for (int i=0;i<s.length();i++) {
            char c = s.charAt(i);
            if (slash) {
                cur.append(c);
                slash = false;
            } else if (c == '\\') {
                slash = true;
            } else if (c == delimiter) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out;
    }
}