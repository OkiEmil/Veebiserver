import java.io.IOException;
import java.util.Map;

// TODO: add logging

public class GetRequestHandler extends RequestHandler {

    public GetRequestHandler() {
        super("GET");
    }

    @Override
    protected Response handleRequest(Request request) {

        try {
            String resource= request.getRequestResource();

            if (!FileHandler.getInstance().fileExists(resource)) {
                return new HttpResponseBuilder()
                        .setHttpVersion(request.getRequestProtocol())
                        .setStatus(HttpStatus.CLIENT_ERROR_404_NOT_FOUND)
                        .build();
            }

            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request))
                    .addHeader("Content-type", FileHandler.getInstance().getMimeType(request.getRequestResource()))
                    .addHeader("Content-length", String.valueOf(FileHandler.getInstance().getFileSize(request.getRequestResource())))
                    .setBody(new byte[0]); // PLACEHOLDER

            return responseBuilder.build();

        } catch (IOException e) {
            return new HttpResponseBuilder()
                    .setHttpVersion(request.getRequestProtocol())
                    .setStatus(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
