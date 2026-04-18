import java.io.IOException;

public class DownloadRequestHandler extends RequestHandler
{
    Logger logger;

    public DownloadRequestHandler(WebrootHandler webrootHandler) {
        super("DOWNLOAD", webrootHandler);

        logger = new Logger("DownloadRequestHandler");
    }

    @Override
    protected Response handleRequest(Request request) {

        logger.log("Trying to send file.", true);

        try {
            String resource = getWEBROOT_HANDLER().getCorrectPath(request.getRequestResource());

            if(!FileHandler.getInstance().fileExists(resource))
            {
                return new HttpResponseBuilder()
                .setHttpVersion(request.getRequestProtocol())
                .setStatus(HttpStatus.CLIENT_ERROR_404_NOT_FOUND)
                .build();
            }

            FilePayload payload = FileHandler.getInstance().createFilePayLoad(resource);

            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request))
            .addHeader("Content-Type", payload.mimeType)
            .addHeader("Content-Length", String.valueOf(payload.content.length))
            .addHeader("Content-Disposition", "attachment; filename=\"" + payload.fileName + "\"")
            .setBody(payload.content);

            return responseBuilder.build();
        }
        catch (IOException exception)
        {
            logger.log("Failed to send file.", true);

            return new HttpResponseBuilder()
                    .setHttpVersion(request.getRequestProtocol())
                    .setStatus(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
