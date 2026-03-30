import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {

    private Socket connectionSocket;

    public ClientHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = connectionSocket.getInputStream();
            OutputStream outputStream = connectionSocket.getOutputStream();

            Request httpRequest = parseRequest(inputStream);
            Response httpResponse = handleRequest(httpRequest);

            outputStream.write(httpResponse.getResponseAsBytes());
            this.connectionSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Request parseRequest(InputStream inputStream) throws IOException {
        HashMap<String, String> requestMap = new HashMap<>();

        InputStream clientInputStream = new BufferedInputStream(inputStream);
        String requestLine = lineFromInputStream(clientInputStream); // the first line of the request

        if (requestLine != null) {
            String[] requestLineComponents = requestLine.split(" ");
            System.out.println(requestLine);
            // for example POST /contact_form.php HTTP/1.1
            String requestMethod = requestLineComponents[0];
            String requestResource = requestLineComponents[1];
            String requestProtocol = requestLineComponents[2];

            // make it easier to access the request info
            requestMap.put("Method", requestMethod);
            requestMap.put("Resource", requestResource);
            requestMap.put("Protocol", requestProtocol);

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
            System.out.println(httpRequest);
            return httpRequest;



        }
        return null;
    }

    private String lineFromInputStream(InputStream inputStream) throws IOException {
        // https://stackoverflow.com/questions/1579823/buffered-reader-http-post


        int byteRead = 0;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while((byteRead = inputStream.read()) != -1) {
            if (byteRead == '\r') { // or == 13
                inputStream.mark(1); // next time read only one byte
                int nextRead = inputStream.read();
                if ( nextRead == '\n') {
                    break;
                } else {
                    inputStream.reset(); // reset the mark
                }
            }
           /* else if (byteRead == '\n') {
                break;
            }*/
            else {
                buffer.write(byteRead);
            }
        }

        return buffer.toString(StandardCharsets.US_ASCII);
    }

    public Response handleRequest(Request request) {

        // Decide how to handle different requests (GET, POST, DELETE, etc..)

        return new HttpResponseBuilder().setHttpVersion(request.getRequestProtocol())
                .setStatus(HttpStatus.SERVER_ERROR_501_NOT_IMPLEMENTED)
                .build();
    }



}
