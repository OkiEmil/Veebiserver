import java.io.IOException;

public class HeadRequestHandler extends RequestHandler{

    private Logger logger;

    public HeadRequestHandler(WebrootHandler webrootHandler) {
        super("HEAD", webrootHandler);
        this.logger = new Logger(ServerConfig.getInstance().readPropertyAsString("log.directory" + "/Requests"))
    }

    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {
        logger.log("HEAD request for: " + request.getRequestResource(), true);

        try {
            String resource= getWEBROOT_HANDLER().getCorrectPath(request.getRequestResource());
            if (!FileHandler.getInstance().fileExists(resource)) {
                logger.log("File not found: " + resource, true);
                return new ErrorPageBuilder(HttpStatus.CLIENT_ERROR_404_NOT_FOUND, "file at path " + resource +" was not found",
                        request.getRequestProtocol().getLITERAL()).buildResponseFromError();
            }

            logger.log("HEAD response - Size: " + FileHandler.getInstance().getFileSize(resource) + " bytes", false);

            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request, sessionManager))
                    .addHeader("Content-type", FileHandler.getInstance().getMimeType(resource))
                    .addHeader("Content-length", String.valueOf(FileHandler.getInstance().getFileSize(resource)));

            return responseBuilder.build();

        } catch (Exception exception) {
            logger.log("ERROR in HEAD request: " + exception.getMessage(), true);
            //logger.log("Failed to handle log request.", true);
            return new ErrorPageBuilder(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR, exception.getLocalizedMessage(),
                    request.getRequestProtocol().getLITERAL()).buildResponseFromError();
        }
    }
}
