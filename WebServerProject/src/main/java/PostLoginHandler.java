import Routing.Router;
import Routing.WebrootHandler;
import UserManagement.Users;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PostLoginHandler extends RequestHandler{

    public PostLoginHandler(Router router) {
        super("/login",router);
    }

    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {
        SessionState sessionState=request.getSessionState();
        if (sessionState.getUsername()!=null) { // if the user is already logged in logs them out
            sessionManager.endSession(sessionState.getSessionId());
        }
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        Users users = Users.getInstance();
        if (users.isPasswordCorrect(username,password)) {
            String sessionId = sessionManager.startSession(username);
            byte[] body="login successful".getBytes(StandardCharsets.UTF_8); // placeholder
            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request,sessionManager))
                    .addHeader("Set-Cookie", "sessionId=" + sessionId + "; HttpOnly; SameSite=Lax; Path=/") // TODO: when https is implemented add secure flag
                    .addHeader("Content-type", "text/html")
                    .addHeader("Content-length", String.valueOf(body.length))
                    .setBody(body);

            return responseBuilder.build();
        }
        else { // login failed
            byte[] body="login failed".getBytes(); // placeholder
            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request, sessionManager))
                    .addHeader("Content-type", "text/html")
                    .addHeader("Content-length", String.valueOf(body.length))
                    .setBody(body);

            return responseBuilder.build();
        }
    }
}
