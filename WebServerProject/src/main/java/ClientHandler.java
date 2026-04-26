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

    private final String CONTINUE = "HTTP/1.1 100 Continue\r\n\r\n";
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

        try {
            connectionSocket.setSoTimeout(5000); // end the connection automatically after 5 seconds of not getting anything
            boolean keepConnection = true;
            BufferedInputStream inputStream = new BufferedInputStream(connectionSocket.getInputStream());
            OutputStream outputStream = connectionSocket.getOutputStream();
            while (keepConnection) {

                try {

                    Request httpRequest = parseRequest(inputStream, outputStream);

                    if (httpRequest == null) {
                        break;
                    } else if ("close".equalsIgnoreCase(httpRequest.getHeader("connection"))) {
                        keepConnection = false;
                    }

                    // cookies session into sessionstate
                    Map<String,String> cookies = httpRequest.getCookies();
                    String sessionId=cookies.get("SESSIONID");
                    String username;
                    if (sessionId!=null) {
                        username = sessionManager.getUsername(sessionId);
                    } else username=null;
                    SessionState sessionState = new SessionState(username,sessionId);
                    httpRequest.setSessionState(sessionState);

                    Response httpResponse = handleRequest(httpRequest);
                    //outputStream.write(httpResponse.getResponseAsBytes());
                    //outputStream.flush();

                    outputStream.write(httpResponse.getHeaderAsBytes());

                    if(httpResponse.getInputStream() != null)
                    {
                        InputStream in = httpResponse.getInputStream();
                        byte[] buffer = new byte[8192];
                        int bytesRead;

                        while((bytesRead = in.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }

                        in.close();
                    }
                    else if(httpResponse.getMessageBody() != null)
                    {
                        outputStream.write(httpResponse.getMessageBody());
                    }

                    outputStream.flush();

                } catch (SocketTimeoutException e){
                    keepConnection = false;
                }
            }

        } catch (Exception ex) {
            if (!(ex instanceof SocketException)) {

                // TODO - implement a way to give a response of the error instead of this
                ex.printStackTrace();
            }
        }
        finally {
            try {
                this.connectionSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Request parseRequest(BufferedInputStream clientInputStream, OutputStream outputStream) throws IOException, HttpParsingException {
        HashMap<String, String> requestMap = new HashMap<>();
        String requestLine = lineFromInputStream(clientInputStream); // the first line of the request
        // for example POST /contact_form.php HTTP/1.1

        if (requestLine == null || requestLine.trim().isEmpty()) {
            return null;
        }


        try {
            String[] requestLineComponents = requestLine.split(" ");


                String requestMethod = requestLineComponents[0];
                String requestResource = requestLineComponents[1];
                String requestProtocol = requestLineComponents[2];
                requestMap.put("method", requestMethod);
                requestMap.put("resource", requestResource);
                requestMap.put("protocol", requestProtocol);


            // the headers
            String headerLine = lineFromInputStream(clientInputStream);
            while (!headerLine.isEmpty()) {
                String[] headerLineComponents = headerLine.split(": ", 2);

                // toLowerCase() is required, since headers are not always of the same case
                requestMap.put(headerLineComponents[0].toLowerCase(), headerLineComponents[1]);

                headerLine = lineFromInputStream(clientInputStream);
            }

            String expectHeader = requestMap.get("expect");
            if (expectHeader != null && expectHeader.equalsIgnoreCase("100-continue")){
                // Before sending the body sometimes needs a 100-continue
                outputStream.write(CONTINUE.getBytes(StandardCharsets.ISO_8859_1));
                outputStream.flush();
            }

            byte[] bodyBytes = null;
            // next comes the body (raw byte input stream to handle different types of data like images or forms)
            String contentLengthString = requestMap.get("content-length");
            if (contentLengthString != null) {
                int contentLength = Integer.parseInt(contentLengthString);
                bodyBytes = new byte[contentLength];

                int alreadyReadBytesAmount = 0;
                while (alreadyReadBytesAmount < contentLength) {
                    int readThisTime = clientInputStream.read(bodyBytes, alreadyReadBytesAmount, contentLength-alreadyReadBytesAmount);

                    if (readThisTime == -1) {
                        break;
                    }
                    alreadyReadBytesAmount += readThisTime;
                }

            }
            Request httpRequest = new Request(bodyBytes, requestMap);
            return httpRequest;
            } catch (Exception e) {
                throw new HttpParsingException(HttpStatus.CLIENT_ERROR_400_BAD_REQUEST);
            }

    }

    private String lineFromInputStream(InputStream inputStream) throws IOException {
        // https://stackoverflow.com/questions/1579823/buffered-reader-http-post

        int byteRead = 0;
        boolean hasReadAnything = false;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while((byteRead = inputStream.read()) != -1) {
            hasReadAnything = true;
            if (byteRead == '\r') { // or == 13
                inputStream.mark(1); // next time read only one byte
                int nextRead = inputStream.read();
                if ( nextRead == '\n') {
                    break;
                } else {
                    inputStream.reset(); // reset the mark
                }
            }
            else if (byteRead == '\n') {
                break;
            } else {
                buffer.write(byteRead);
            }
        }
        if (!hasReadAnything) {
            return null;
        }

        return buffer.toString(StandardCharsets.ISO_8859_1);


    }

    public Response handleRequest(Request request) {
        System.out.println("Handling: " + request.getHeader("method") + " method for resource " + request.getRequestResource());

        try {
            // The Host: header MUST be included according to the protocol
            if (request.getHeader("host") == null) {
                return new HttpResponseBuilder().setHttpVersion(request.getRequestProtocol())
                        .setStatus(HttpStatus.CLIENT_ERROR_400_BAD_REQUEST)
                        .setBody("<html><body> <h2>No Host: header received</h2> HTTP 1.1 requests must include the Host: header. </body></html>".getBytes(StandardCharsets.ISO_8859_1))
                        .build();
            }

            String method = request.getHeader("method");
            for (RequestHandler requestHandler : requestHandlers) {
                if (requestHandler.canHandle(request)) {
                    return requestHandler.handleRequest(request);
                }
            }


            return new HttpResponseBuilder().setHttpVersion(request.getRequestProtocol())
                    .setStatus(HttpStatus.SERVER_ERROR_501_NOT_IMPLEMENTED)
                    .setBody(("method" + method + " not implemented").getBytes())
                    .build();
        } catch (Exception e) {
            return new HttpResponseBuilder().setHttpVersion(request.getRequestProtocol())
                    .setStatus(HttpStatus.SERVER_ERROR_500_INTERNAL_SERVER_ERROR)
                    .build();
        }

    }



}
