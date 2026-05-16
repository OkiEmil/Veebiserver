import Routing.Router;
import Routing.WebrootHandler;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public abstract class RequestHandler {

    private final String HANDLER_METHOD;
    private final Router ROUTER;

    public RequestHandler(String handlerMethod, Router router) {
        this.HANDLER_METHOD = handlerMethod;
        this.ROUTER = router;
    }

    protected Response handleRequest(Request request, SessionManager sessionManager) {
        HttpResponseBuilder responseBuilder = new HttpResponseBuilder()
                .setHttpVersion(request.getRequestProtocol().getLITERAL())
                .setStatus(HttpStatus.OK)
                .addHeader("Date", getDateTime());
        return responseBuilder.build();
    }

    public boolean canHandle(Request request) {
        return request.getRequestMethod().equalsIgnoreCase(HANDLER_METHOD);
    }

    public Router getRouter() {
        return this.ROUTER;
    }

    private String getDateTime() {
        return ZonedDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }
}