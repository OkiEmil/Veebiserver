import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.spec.RSAOtherPrimeInfo;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ClientHandler implements Runnable {

    private Socket connectionSocket;
    private WebrootHandler webrootHandler;

    public ClientHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        this.webrootHandler = new WebrootHandler("public");
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

                    Request httpRequest = parseRequest(inputStream);

                    if (httpRequest == null) {
                        break;
                    } else if ("close".equalsIgnoreCase(httpRequest.getHeader("connection"))) {
                        keepConnection = false;
                    }
                    Response httpResponse = handleRequest(httpRequest);
                    outputStream.write(httpResponse.getResponseAsBytes());
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

    private Request parseRequest(BufferedInputStream clientInputStream) throws IOException, HttpParsingException {
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
            // Decide how to better handle different requests (GET, POST, DELETE, etc..)
            String method = request.getHeader("method");
            if (method.equalsIgnoreCase("GET")) {
                GetRequestHandler getHandler = new GetRequestHandler(webrootHandler);
                return getHandler.handleRequest(request);
            }
            if (method.equalsIgnoreCase("HEAD")) {
                HeadRequestHandler headHandler = new HeadRequestHandler(webrootHandler);
                return headHandler.handleRequest(request);
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
