import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public abstract class RequestHandler {

    private final String HANDLER_METHOD;

    public RequestHandler(String handlerMethod) {
        this.HANDLER_METHOD = handlerMethod;
    }

    protected Response handleRequest(Request request) {
        HttpResponseBuilder responseBuilder = new HttpResponseBuilder()
                .setHttpVersion(request.getRequestProtocol())
                .setStatus(HttpStatus.OK)
                .addHeader("Date", ZonedDateTime.now(ZoneOffset.UTC)
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME));
        return responseBuilder.build();
    }

    public boolean canHandle(Request request) {
        return request.getRequestMethod().equals(HANDLER_METHOD);
    }
}