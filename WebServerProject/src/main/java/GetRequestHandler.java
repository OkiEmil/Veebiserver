import Routing.Route;
import Routing.Router;
import Routing.WebrootHandler;
import UserManagement.Users;

// TODO: add logging

public class GetRequestHandler extends RequestHandler {
    private final Router router;
    //Logger logger;

    public GetRequestHandler(Router router) {
        super("GET", null);
        this.router=router;
        //logger = new Logger("GetRequestHandler");
    }

    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {

        //logger.log("Trying to handle get request.", true);

        Logger.logStatic(ENamedStaticLogger.REQUEST_GET, "Trying to handle get reequest.", true);

        try {
            if (request.getRequestResource().equalsIgnoreCase("/logout")) {
                return new GetLogoutHandler(null).handleRequest(request,sessionManager);
            }
            System.out.println("resource to find route by: " + request.getRequestResource());
            Route route = router.resolve(request.getRequestResource());
            if (route == null) {
                return new ErrorPageBuilder(
                        HttpStatus.CLIENT_ERROR_404_NOT_FOUND,
                        "Route not found",
                        request.getRequestProtocol().getLITERAL())
                        .buildResponseFromError();
            }
            System.out.println("accesslevel: " + route.getAccessLevel());
            if (route.getAccessLevel()>0) {
                String username = request.getSessionState().getUsername();
                if (username==null || Users.getInstance().findUser(username).getAccessLevel() < route.getAccessLevel()) {
                    return new ErrorPageBuilder(HttpStatus.CLIENT_ERROR_403_FORBIDDEN, "Forbidden",
                            request.getRequestProtocol().getLITERAL()).buildResponseFromError();
                }
            }
            String relativePath = request.getRequestResource().substring(route.getPathPrefix().length());
            if (relativePath.isEmpty()) {
                relativePath = "/";
            }
            WebrootHandler webrootHandler = route.getWebrootHandler();
            String resource= webrootHandler.getCorrectPath(relativePath);
            if (!FileHandler.getInstance().fileExists(resource)) {
                return new ErrorPageBuilder(HttpStatus.CLIENT_ERROR_404_NOT_FOUND, "file at path " + resource +" was not found",
                        request.getRequestProtocol().getLITERAL()).buildResponseFromError();
            }

            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request, sessionManager))
                    .addHeader("Content-type", FileHandler.getInstance().getMimeType(resource))
                    .addHeader("Content-length", String.valueOf(FileHandler.getInstance().getFileSize(resource)))
                    .setBody(webrootHandler.getByteArray(relativePath));

            return responseBuilder.build();

        } catch (Exception exception) {
            //logger.log("Failed to handle log request.", true);
            Logger.logStatic(ENamedStaticLogger.REQUEST_GET, "Failed to handle log request.", true);

            return new ErrorPageBuilder(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR, exception.getLocalizedMessage(),
                    request.getRequestProtocol().getLITERAL()).buildResponseFromError();
        }
    }
}
