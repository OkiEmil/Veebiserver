import java.io.IOException;

public class HeadRequestHandler extends RequestHandler{

    public HeadRequestHandler(WebrootHandler webrootHandler) {
        super("HEAD", webrootHandler);
    }

    @Override
    protected Response handleRequest(Request request) {

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
                    .addHeader("Content-length", String.valueOf(FileHandler.getInstance().getFileSize(resource)));

            return responseBuilder.build();

        } catch (IOException e) {
            return new HttpResponseBuilder()
                    .setHttpVersion(request.getRequestProtocol())
                    .setStatus(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
