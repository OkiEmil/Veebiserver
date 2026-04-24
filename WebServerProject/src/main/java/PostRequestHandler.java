import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Supplier;

public class PostRequestHandler extends RequestHandler {

    Logger logger;
    private final Map<String, Supplier<PostRequestHandler>> IMPLEMENTED_RESOURCES;

    public PostRequestHandler(WebrootHandler webrootHandler)
    {
        super("POST", webrootHandler);
        logger = new Logger("PostRequestHandler");

        IMPLEMENTED_RESOURCES = Map.of(
                "/login", () -> new PostLoginHandler(webrootHandler),
                "/register", () -> new PostRegisterHandler(webrootHandler)
        );
    }

    @Override
    protected Response handleRequest(Request request) {

        try {
            String resource = getWEBROOT_HANDLER().getCorrectPath(request.getRequestResource());

            if (IMPLEMENTED_RESOURCES.containsKey(resource)) {
                try {
                    return IMPLEMENTED_RESOURCES.get(resource).get().handleRequest(request);

                } catch (Exception exception) {
                    logger.log(exception.getLocalizedMessage(), true);

                    return new HttpResponseBuilder()
                            .setHttpVersion(request.getRequestProtocol())
                            .setStatus(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                            .build();
                }
            }


            return new HttpResponseBuilder()
                    .setHttpVersion(request.getRequestProtocol())
                    .setStatus(HttpStatus.SERVER_ERROR_501_NOT_IMPLEMENTED)
                    .build();


        } catch (Exception exception) {
            logger.log(exception.getLocalizedMessage(), true);

            return new HttpResponseBuilder()
                    .setHttpVersion(request.getRequestProtocol())
                    .setStatus(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public boolean canHandle(Request request) {
        try {
            return "POST".equals(request.getRequestMethod()) && IMPLEMENTED_RESOURCES
                    .containsKey(getWEBROOT_HANDLER().getCorrectPath(request.getRequestResource()));
        } catch (FileNotFoundException e) {return false;}
    }
}
