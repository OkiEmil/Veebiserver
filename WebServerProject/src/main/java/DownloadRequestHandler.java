import Routing.Route;
import Routing.Router;
import Routing.WebrootHandler;
import UserManagement.Users;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class DownloadRequestHandler extends RequestHandler {
    //Logger logger;

    public DownloadRequestHandler(Router router) {
        super("DOWNLOAD", router);

        //logger = new Logger("DownloadRequestHandler");
    }

    @Override
    protected Response handleRequest(Request request,SessionManager sessionManager) {
        Logger.logStatic(ENamedStaticLogger.REQUEST_DOWNLOAD, "Trying to handle download reequest.", true);
        try {
            Route route = getRouter().resolve(request.getRequestResource());

            if (route==null) {
                return errorNotFound(request);
            }
            if (Users.getInstance().findUser(request.getSessionState().getUsername()).getAccessLevel() < route.getAccessLevel()) {
                return new ErrorPageBuilder(HttpStatus.CLIENT_ERROR_403_FORBIDDEN, "Forbidden",
                        request.getRequestProtocol().getLITERAL()).buildResponseFromError();
            }
            String relativePath = request.getRequestResource().substring(route.getPathPrefix().length());
            if (relativePath.isEmpty()) {
                return errorNotFound(request);
            }
            WebrootHandler webrootHandler = route.getWebrootHandler();
            String resource= webrootHandler.getCorrectPath(relativePath);
            if (!FileHandler.getInstance().fileExists(resource)) {
                return new ErrorPageBuilder(HttpStatus.CLIENT_ERROR_404_NOT_FOUND, "file at path " + resource +" was not found",
                        request.getRequestProtocol().getLITERAL()).buildResponseFromError();
            }
            File file = new File(resource);
            InputStream inputStream = new FileInputStream(file);

            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request,sessionManager))
                    .addHeader("Content-Type", FileHandler.getInstance().getMimeType(resource))
                    .addHeader("Content-Length", String.valueOf(file.length()))
                    .addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                    .setInputStream(inputStream);

            return responseBuilder.build();
        } catch (Exception exception) {
            Logger.logStatic(ENamedStaticLogger.REQUEST_DOWNLOAD, "Trying to handle download reequest.", true);
            return new HttpResponseBuilder()
                    .setHttpVersion(request.getRequestProtocol().getLITERAL())
                    .setStatus(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
    private Response errorNotFound(Request request) {
        return new ErrorPageBuilder(
                HttpStatus.CLIENT_ERROR_404_NOT_FOUND,
                "Route not found",
                request.getRequestProtocol().getLITERAL())
                .buildResponseFromError();
    }


    /*@Override
    protected Response handleRequest(Request request) {

        logger.log("Trying to send file.", true);

        try {
            String resource = getWEBROOT_HANDLER().getCorrectPath(request.getRequestResource());

            if(!FileHandler.getInstance().fileExists(resource))
            {
                return new ErrorPageBuilder(HttpStatus.CLIENT_ERROR_404_NOT_FOUND, "file at path " + resource +" was not found",
                        request.getRequestProtocol()).buildResponseFromError();
            }

            FilePayload payload = FileHandler.getInstance().createFilePayLoad(resource);

            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request,sessionManager))
            .addHeader("Content-Type", payload.mimeType)
            .addHeader("Content-Length", String.valueOf(payload.content.length))
            .addHeader("Content-Disposition", "attachment; filename=\"" + payload.fileName + "\"")
            .setBody(payload.content);

            return responseBuilder.build();
        }
        catch (IOException exception)
        {
            //logger.log("Failed to send file.", true);

            return new ErrorPageBuilder(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR, exception.getLocalizedMessage(),
                    request.getRequestProtocol()).buildResponseFromError();
        }
    }*/
}
