import Routing.Router;
import Routing.WebrootHandler;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Supplier;

public class PostRequestHandler extends RequestHandler {

    Logger logger;
    private final Map<String, Supplier<RequestHandler>> IMPLEMENTED_RESOURCES;

    public PostRequestHandler(Router router)
    {
        super("POST", router);
        logger = new Logger("PostRequestHandler");

        IMPLEMENTED_RESOURCES = Map.of(
                "/login", () -> new PostLoginHandler(null),
                "/register", () -> new PostRegisterHandler(null)
        );
    }

    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {
        try {
            String resource = request.getRequestResource();

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
        return "POST".equalsIgnoreCase(request.getRequestMethod()) && IMPLEMENTED_RESOURCES
                .containsKey(request.getRequestResource());
    }
}
