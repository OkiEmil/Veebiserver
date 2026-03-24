import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {

    private Socket connectionSocket;
    private Request httpRequest;

    public ClientHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    private void parseRequest() throws IOException {
        Map<String, String> requestMap = new HashMap<>();

        InputStream clientInputStream = new BufferedInputStream(connectionSocket.getInputStream());
        String requestLine = lineFromInputStream(clientInputStream); // the first line of the request

        if (requestLine != null) {
            String[] requestLineComponents = requestLine.split(" ");

            // for example POST /contact_form.php HTTP/1.1
            String requestMethod = requestLineComponents[0];
            String requestResource = requestLineComponents[1];
            String requestProtocol = requestLineComponents[2];

            // make it easier to access the request info
            requestMap.put("Method", requestMethod);
            requestMap.put("Resource", requestResource);
            requestMap.put("Protocol", requestProtocol);

            // next come the headers
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
            httpRequest = new Request(bodyBytes, requestMap);
            System.out.println(httpRequest);


        }
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
            else if (byteRead == '\n') {
                break;
            }
            else {
                buffer.write(byteRead);
            }

        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void run() {
        try {
            parseRequest();

            this.connectionSocket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}
