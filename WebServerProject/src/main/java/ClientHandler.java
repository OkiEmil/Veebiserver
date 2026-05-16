import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {


    private final Socket connectionSocket;
    private final WebrootHandler webrootHandler;
    private final SessionManager sessionManager;
    private final List<RequestHandler> requestHandlers;

    public ClientHandler(Socket connectionSocket, SessionManager sessionManager) {
        this.connectionSocket = connectionSocket;
        this.sessionManager = sessionManager;
        this.webrootHandler = new WebrootHandler("public");
        this.requestHandlers = loadRequestHandlers();
    }

    private List<RequestHandler> loadRequestHandlers() {
        return Arrays.asList(
                new GetRequestHandler(webrootHandler),
                new HeadRequestHandler(webrootHandler),
                new DownloadRequestHandler(webrootHandler),
                new PostRequestHandler(webrootHandler)
        );
    }

    @Override
    public void run() {
        OutputStream outputStream = null;
        try {
            connectionSocket.setSoTimeout(ServerConfig.getInstance().getSessionTimeout());
            //connectionSocket.setSoTimeout(5000); // end the connection automatically after 5 seconds of not getting anything
            boolean keepConnection = true;
            BufferedInputStream inputStream = new BufferedInputStream(connectionSocket.getInputStream());
            outputStream = connectionSocket.getOutputStream();
            while (keepConnection) {

                try {

                    HttpParser httpParser = new HttpParser();

                    Request httpRequest = httpParser.parseRequest(inputStream, outputStream);
                    HttpVersion httpVersion = httpRequest.getRequestProtocol();
                    System.out.println(new String(httpRequest.getMessageBody()) + " BODD");

                    if (httpRequest == null) {
                        break;
                    } else if ("close".equalsIgnoreCase(httpRequest.getHeader("connection"))
                            || !httpVersion.allows(HttpVersion.Feature.PERSISTENCE)) {
                        keepConnection = false;
                    }

                    // cookies session into sessionstate
                    Map<String,String> cookies = httpRequest.getCookies();
                    String sessionId=cookies.get("sessionid");
                    String username;
                    if (sessionId!=null) {
                        username = sessionManager.getUsername(sessionId);
                    } else username=null;
                    SessionState sessionState = new SessionState(username,sessionId);
                    httpRequest.setSessionState(sessionState);

                    Response httpResponse = handleRequest(httpRequest);

                    if (!httpVersion.allows(HttpVersion.Feature.CHUNKING)) {
                        outputStream.write(httpResponse.getResponseAsBytes());
                        outputStream.flush();

                    } else {

                        outputStream.write(httpResponse.getHeaderAsBytes());

                        if (httpResponse.getInputStream() != null) {
                            InputStream in = httpResponse.getInputStream();
                            int bufferSize = ServerConfig.getInstance().readPropertyAsInt("buffersize");
                            byte[] buffer = new byte[bufferSize];
                            //byte[] buffer = new byte[8192];
                            int bytesRead;

                            while ((bytesRead = in.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }

                            in.close();
                        } else if (httpResponse.getMessageBody() != null) {
                            outputStream.write(httpResponse.getMessageBody());
                        }

                        outputStream.flush();
                    }

                } catch (SocketTimeoutException e){
                    keepConnection = false;
                }
            }

        } catch (Exception ex) {
            if (!(ex instanceof SocketException)) {
                try {
                    if (outputStream != null) {
                        outputStream.write(new ErrorPageBuilder(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR, "HTTP/1.1")
                                .buildResponseFromError().getResponseAsBytes());
                        outputStream.flush();
                    }

                } catch (Exception ignored) {

                }
            }
        }
        finally {
            try {
                this.connectionSocket.close();
            } catch (IOException ignored) {

            }
        }
    }

    public Response handleRequest(Request request) {
        System.out.println("Handling: " + request.getHeader("method") + " method for resource " + request.getRequestResource());

        try {
            // The Host: header MUST be included according to the protocol

            if (request.getRequestProtocol().allows(HttpVersion.Feature.HOST_HEADER_REQUIRED) &&  request.getHeader("host") == null) {
                return new ErrorPageBuilder(HttpStatus.CLIENT_ERROR_400_BAD_REQUEST,
                        "No Host: header received, HTTP 1.1 requests must include the Host: header.",
                        request.getRequestProtocol().getLITERAL()).buildResponseFromError();
            }

            String method = request.getHeader("method");
            for (RequestHandler requestHandler : this.requestHandlers) {
                if (requestHandler.canHandle(request)) {
                    return requestHandler.handleRequest(request, this.sessionManager);
                }
            }

            return new ErrorPageBuilder(HttpStatus.SERVER_ERROR_501_NOT_IMPLEMENTED,
                    "method " + method + " not implemented",
                    request.getRequestProtocol().getLITERAL()).buildResponseFromError();
        } catch (Exception e) {
            return new ErrorPageBuilder(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR,
                    request.getRequestProtocol().getLITERAL()).buildResponseFromError();
        }

    }



}
