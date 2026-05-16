import Routing.Router;

public class GetLogoutHandler extends RequestHandler{
    public GetLogoutHandler(Router router) {
        super("/logout",router);
    }
    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {
        SessionState sessionState=request.getSessionState();
        if (sessionState.getUsername()!=null) { // ends session in sessionManager
            sessionManager.endSession(sessionState.getSessionId());
        }
        byte[] body = "logged out".getBytes();
        HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request,sessionManager))
                .addHeader("Set-Cookie", "sessionId=; Max-Age=0; HttpOnly; SameSite=Lax; Path=/") // deletes session cookie
                .addHeader("Content-type", "text/html")
                .addHeader("Content-length", String.valueOf(body.length))
                .setBody(body);

        return responseBuilder.build();
    }
}
