import java.io.FileNotFoundException;
import java.io.IOException;
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
                "public/login", () -> new PostLoginHandler(webrootHandler),
                "public/register", () -> new PostRegisterHandler(webrootHandler)
        );
    }

    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {
        try {
            String resource = getWEBROOT_HANDLER().getCorrectPath(request.getRequestResource());

            if (IMPLEMENTED_RESOURCES.containsKey(resource)) {
                try {
                    return IMPLEMENTED_RESOURCES.get(resource).get().handleRequest(request, sessionManager);

                } catch (Exception exception) {
                    logger.log(exception.getLocalizedMessage(), true);

                    return new ErrorPageBuilder(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR, exception.getLocalizedMessage(),
                                request.getRequestProtocol().getLITERAL()).buildResponseFromError();

                }
            }


            return new ErrorPageBuilder(HttpStatus.SERVER_ERROR_501_NOT_IMPLEMENTED,
                    request.getRequestProtocol().getLITERAL()).buildResponseFromError();


        } catch (Exception exception) {
            logger.log(exception.getLocalizedMessage(), true);

            return new ErrorPageBuilder(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR, exception.getLocalizedMessage(),
                    request.getRequestProtocol().getLITERAL()).buildResponseFromError();
        }
    }

    @Override
    public boolean canHandle(Request request) {
        try {
            return "POST".equalsIgnoreCase(request.getRequestMethod()) && IMPLEMENTED_RESOURCES
                    .containsKey(getWEBROOT_HANDLER().getCorrectPath(request.getRequestResource()));
        } catch (FileNotFoundException e) {return false;}
    }
}
