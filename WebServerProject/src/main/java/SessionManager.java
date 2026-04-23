import java.net.CookieManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;

public class SessionManager {
    private final Map<String,Session> sessions = new ConcurrentHashMap<>();

    static class Session {
        String username;
        long sessionStart;
        public Session(String username,long sessionStart) {
            this.username=username;
            this.sessionStart=sessionStart;
        }

        public String getUsername() {
            return username;
        }

        public long getSessionStart() {
            return sessionStart;
        }
    }
    public String startSession(String username) {
        String sessionId = randomUUID().toString();
        sessions.put(sessionId,new Session(username,currentTimeMillis()));
        return sessionId;
    }

    public String getUsername(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session==null) return null;
        double timeoutTimer = 3600000;
        if (session.getSessionStart() + timeoutTimer < currentTimeMillis()) {
            sessions.remove(sessionId);
            return null;
        } else {
            return session.getUsername();
        }
    }
    public void endSession(String sessionId) {
        if (sessions.get(sessionId)!=null) sessions.remove(sessionId);
    }
}
