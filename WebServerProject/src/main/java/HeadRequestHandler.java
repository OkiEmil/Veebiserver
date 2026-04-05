import java.io.IOException;

public class HeadRequestHandler extends RequestHandler{

    public HeadRequestHandler() {
        super("HEAD");
    }

    @Override
    protected Response handleRequest(Request request) {

        try {
            if (!FileHandler.getInstance().fileExists(request.getRequestResource())) {
                return new HttpResponseBuilder()
                        .setHttpVersion(request.getRequestProtocol())
                        .setStatus(HttpStatus.CLIENT_ERROR_404_NOT_FOUND)
                        .build();
            }

            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request))
                    .addHeader("Content-type", FileHandler.getInstance().getMimeType(request.getRequestResource()))
                    .addHeader("Content-length", String.valueOf(FileHandler.getInstance().getFileSize(request.getRequestResource())));

            return responseBuilder.build();

        } catch (IOException e) {
            return new HttpResponseBuilder()
                    .setHttpVersion(request.getRequestProtocol())
                    .setStatus(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
