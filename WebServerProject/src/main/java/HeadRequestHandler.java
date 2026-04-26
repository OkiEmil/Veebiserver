import java.io.IOException;

public class HeadRequestHandler extends RequestHandler{

    public HeadRequestHandler(WebrootHandler webrootHandler) {
        super("HEAD", webrootHandler);
    }

    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {

        try {
            String resource= getWEBROOT_HANDLER().getCorrectPath(request.getRequestResource());
            if (!FileHandler.getInstance().fileExists(resource)) {
                return new ErrorPageBuilder(HttpStatus.CLIENT_ERROR_404_NOT_FOUND, "file at path " + resource +" was not found",
                        request.getRequestProtocol()).buildResponseFromError();
            }

            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request, sessionManager))
                    .addHeader("Content-type", FileHandler.getInstance().getMimeType(resource))
                    .addHeader("Content-length", String.valueOf(FileHandler.getInstance().getFileSize(resource)));

            return responseBuilder.build();

        } catch (Exception exception) {
            //logger.log("Failed to handle log request.", true);
            return new ErrorPageBuilder(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR, exception.getLocalizedMessage(),
                    request.getRequestProtocol()).buildResponseFromError();
        }
    }
}
