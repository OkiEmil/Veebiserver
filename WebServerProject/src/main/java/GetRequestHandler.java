import java.io.IOException;

// TODO: add logging

public class GetRequestHandler extends RequestHandler {

    //Logger logger;

    public GetRequestHandler(WebrootHandler webrootHandler) {
        super("GET", webrootHandler);

        //logger = new Logger("GetRequestHandler");
    }

    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {

        //logger.log("Trying to handle get request.", true);

        Logger.logStatic(ENamedStaticLogger.REQUEST_GET, "Trying to handle get reequest.", true);

        try {

            String resource= getWEBROOT_HANDLER().getCorrectPath(request.getRequestResource());
            if (!FileHandler.getInstance().fileExists(resource)) {
                return new ErrorPageBuilder(HttpStatus.CLIENT_ERROR_404_NOT_FOUND, "file at path " + resource +" was not found",
                        request.getRequestProtocol()).buildResponseFromError();
            }

            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request, sessionManager))
                    .addHeader("Content-type", FileHandler.getInstance().getMimeType(resource))
                    .addHeader("Content-length", String.valueOf(FileHandler.getInstance().getFileSize(resource)))
                    .setBody(getWEBROOT_HANDLER().getByteArray(request.getRequestResource())); // PLACEHOLDER

            return responseBuilder.build();

        } catch (Exception exception) {
            //logger.log("Failed to handle log request.", true);
            Logger.logStatic(ENamedStaticLogger.REQUEST_GET, "Failed to handle log request.", true);

            return new ErrorPageBuilder(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR, exception.getLocalizedMessage(),
                    request.getRequestProtocol()).buildResponseFromError();
        }
    }
}
