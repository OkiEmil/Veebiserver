import Routing.Router;
import Routing.WebrootHandler;
import UserManagement.Users;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PostLoginHandler extends RequestHandler{


    private Logger logger;

    public PostLoginHandler(Router router) {
        super("/login",router);
        this.logger = new Logger(ServerConfig.getInstance().readPropertyAsString("log.directory" + "/Authentication"));

    }

    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {
        SessionState sessionState=request.getSessionState();
        if (sessionState.getUsername()!=null) { // if the user is already logged in logs them out
            logger.log("User '" + sessionState.getUsername() + "' logged out (forced re-login)", true);
            sessionManager.endSession(sessionState.getSessionId());
        }
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Users users = Users.getInstance();
        if (users.isPasswordCorrect(username,password)) {
            String sessionId = sessionManager.startSession(username);

            logger.log("LOGIN SUCCESSFUL - User: " + username, true);
            logger.log("Session ID: " + sessionId, false);

            byte[] body="login successful".getBytes(StandardCharsets.UTF_8); // placeholder
            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request,sessionManager))
                    .addHeader("Set-Cookie", "sessionId=" + sessionId + "; HttpOnly; SameSite=Lax; Path=/") // TODO: when https is implemented add secure flag
                    .addHeader("Content-type", "text/html")
                    .addHeader("Content-length", String.valueOf(body.length))
                    .setBody(body);

            return responseBuilder.build();
        }
        else { // login failed

            logger.log("✗ LOGIN FAILED - User: " + username + " (wrong password)", true);
            byte[] body="login failed".getBytes(); // placeholder
            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request, sessionManager))
                    .addHeader("Content-type", "text/html")
                    .addHeader("Content-length", String.valueOf(body.length))
                    .setBody(body);

            return responseBuilder.build();
        }
    }
}
