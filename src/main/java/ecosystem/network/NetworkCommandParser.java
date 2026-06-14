package ecosystem.network;

import java.util.Map;

/**
 * Converts parsed EntityMessage to a NetworkCommand. Keep minimal — add more commands later.
 */
public final class NetworkCommandParser {

    private NetworkCommandParser() {}

    public static NetworkCommand parse(EntityMessage msg) {
        if (msg == null) return null;

        switch (msg.getAction()) {
            case SPAWN:
                return parseSpawn(msg);
            // add other cases: MOVE, UPDATE, REMOVE ...
            default:
                return null;
        }
    }

    private static NetworkCommand parseSpawn(EntityMessage msg) {
        Map<String,String> p = msg.getPayload();
        String type = msg.getType();
        if (type == null || type.isEmpty()) throw new IllegalArgumentException("Missing type for SPAWN");

        int x = parseIntOrThrow(p.get("x"), "x");
        int y = parseIntOrThrow(p.get("y"), "y");
        int energy = parseIntOrDefault(p.get("energy"), 0);

        return new SpawnCommand(type, x, y, energy);
    }

    private static int parseIntOrDefault(String s, int def) {
        if (s == null || s.isEmpty()) return def;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return def; }
    }

    private static int parseIntOrThrow(String s, String name) {
        if (s == null || s.isEmpty()) throw new IllegalArgumentException("Missing required integer field: " + name);
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid integer for " + name + ": " + s); }
    }
}