import java.io.IOException;
import java.util.Map;

// TODO: add logging

public class GetRequestHandler extends RequestHandler {

    //Logger logger;

    public GetRequestHandler(WebrootHandler webrootHandler) {
        super("GET", webrootHandler);

        //logger = new Logger("GetRequestHandler");
    }

    @Override
    protected Response handleRequest(Request request) {

        //logger.log("Trying to handle get request.", true);

        try {
            String resource= getWEBROOT_HANDLER().getCorrectPath(request.getRequestResource());

            if (!FileHandler.getInstance().fileExists(resource)) {
                return new HttpResponseBuilder()
                        .setHttpVersion(request.getRequestProtocol())
                        .setStatus(HttpStatus.CLIENT_ERROR_404_NOT_FOUND)
                        .build();
            }

            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request))
                    .addHeader("Content-type", FileHandler.getInstance().getMimeType(resource))
                    .addHeader("Content-length", String.valueOf(FileHandler.getInstance().getFileSize(resource)))
                    .setBody(getWEBROOT_HANDLER().getByteArray(request.getRequestResource())); // PLACEHOLDER

            return responseBuilder.build();

        } catch (IOException e) {
            //logger.log("Failed to handle log request.", true);
            return new HttpResponseBuilder()
                    .setHttpVersion(request.getRequestProtocol())
                    .setStatus(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
