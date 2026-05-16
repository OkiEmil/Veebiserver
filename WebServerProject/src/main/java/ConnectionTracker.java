import java.util.concurrent.ConcurrentHashMap;

public class ConnectionTracker {
    private final ConcurrentHashMap<String, Integer> activeConnections;
    private static final int MAX_CONNECTIONS_PER_IP = 5;

    public ConnectionTracker() {
        this.activeConnections = new ConcurrentHashMap<>();
    }

    public synchronized boolean isConnectionOk(String ipAddress) {
        int current = activeConnections.getOrDefault(ipAddress, 0);
        if (current >= MAX_CONNECTIONS_PER_IP) {
            return false;
        }
        activeConnections.put(ipAddress, current + 1);
        return true;
    }

    public synchronized void untrackConnection(String ipAddress) {

        if (!activeConnections.containsKey(ipAddress)) return;

        int current = activeConnections.get(ipAddress);

        if (current <= 1) {
            activeConnections.remove(ipAddress);
        } else {
            activeConnections.put(ipAddress, current - 1);
        }
    }
}