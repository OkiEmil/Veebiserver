import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GetLogoutHandler extends GetRequestHandler{
    public GetLogoutHandler(WebrootHandler webrootHandler) {
        super(webrootHandler);
    }
    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {
        SessionState sessionState=request.getSessionState();
        if (sessionState.getUsername()!=null) { // ends session in sessionManager
            sessionManager.endSession(sessionState.getSessionId());
        }
        byte[] body = "logged out".getBytes();
        HttpResponseBuilder responseBuilder = new HttpResponseBuilder()
                .setHttpVersion(request.getRequestProtocol())
                .setStatus(HttpStatus.OK)
                .addHeader("Date", ZonedDateTime.now(ZoneOffset.UTC)
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader("Set-Cookie", "sessionId=; Max-Age=0; HttpOnly; SameSite=Lax; Path=/") // deletes session cookie
                .addHeader("Content-type", "text/html")
                .addHeader("Content-length", String.valueOf(body.length))
                .setBody(body);

        return responseBuilder.build();
    }
}
