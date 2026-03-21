import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {

    private Socket connectionSocket;
    private Map<String, String> requestMap;
    private Request httpRequest;

    public ClientHandler(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        this.requestMap = new HashMap<>();
    }

    private void parseRequest() throws IOException {
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        String requestLine = clientReader.readLine(); // the first line of the request

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
            String headerLine = clientReader.readLine();
            while (!headerLine.isEmpty()) {
                String[] headerLineComponents = headerLine.split(": ");

                // toLowerCase() is required, since headers are not always of the same case
                requestMap.put(headerLineComponents[0].toLowerCase(), headerLineComponents[1]);

                headerLine = clientReader.readLine();
            }

            byte[] bodyBytes = null;
            // next comes the body (raw byte input stream to handle different types of data like images or forms)
            String contentLengthString = requestMap.get("content-length");
            if (contentLengthString != null) {
                int contentLength = Integer.parseInt(contentLengthString);
                InputStream inputStream = connectionSocket.getInputStream();
                bodyBytes = new byte[contentLength];

                int alreadyReadBytesAmount = 0;
                while (alreadyReadBytesAmount < contentLength) {
                    int readThisTime = inputStream.read(bodyBytes, alreadyReadBytesAmount, contentLength-alreadyReadBytesAmount);

                    if (readThisTime == -1) {
                        break;
                    }
                    alreadyReadBytesAmount += readThisTime;
                }

            }
            httpRequest = new Request(bodyBytes, requestMap);



        }
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
