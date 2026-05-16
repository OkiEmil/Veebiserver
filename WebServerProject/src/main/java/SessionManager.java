import java.net.CookieManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;
import static java.util.UUID.randomUUID;

public class SessionManager {
    private final Map<String,Session> sessions = new ConcurrentHashMap<>();
    private Logger logger;

    public SessionManager() {
        this.logger = new Logger("log.directory" + "/Sessions");
    }

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
        logger.log("Session created - ID: " + sessionId + ", User: " + username, true);
        return sessionId;
    }

    public String getUsername(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session==null) {
                logger.log("Invalid session ID requested: " + sessionId, true);
                return null;
            }
        double timeoutTimer = 3600000;
        if (session.getSessionStart() + timeoutTimer < currentTimeMillis()) {
            logger.log("Session expired - ID: " + sessionId + ", User: " + session.getUsername(), true);
            sessions.remove(sessionId);
            return null;
        } else {
            return session.getUsername();
        }
    }
    public void endSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (sessions.get(sessionId)!=null) {
                logger.log("Session ended - ID: " + sessionId + ", User: " + session.getUsername(), true);
                sessions.remove(sessionId);
            }
    }
}
