public class SessionState {
    private final String username;
    private final String sessionId;

    public SessionState(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }
}
