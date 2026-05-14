import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParser {

    private final String CONTINUE = "HTTP/1.1 100 Continue\r\n\r\n";
    private static final int SP = 0x20; // 32
    private static final int CR = 0x0D; // 13
    private static final int LF = 0x0A; // 10


    public Request parseRequest(InputStream inputStream, OutputStream outputStream) throws HttpParsingException {
        HashMap<String, String> requestMap = new HashMap<>();
        try {
            parseRequestLine(inputStream, requestMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            parseHeaders(inputStream, requestMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String expectHeader = requestMap.get("expect");
        if (expectHeader != null && expectHeader.equalsIgnoreCase("100-continue")){
            // Before sending the body sometimes needs a 100-continue
            try {
                outputStream.write(CONTINUE.getBytes(StandardCharsets.ISO_8859_1));
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        byte[] body = null;
        try {
            body = parseBody(inputStream, requestMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Request request = new Request(body, requestMap);
        return request;
    }

    private void parseRequestLine(InputStream inputStream ,HashMap<String, String> requestMap ) throws IOException, HttpParsingException {
        String requestLine = lineFromInputStream(inputStream);
        if (requestLine == null) {
            throw new HttpParsingException(HttpStatus.CLIENT_ERROR_400_BAD_REQUEST);
        }

        try {
            String[] requestLineComponents = requestLine.split(" ");

            if (requestLineComponents.length != 3)
            {
                throw new HttpParsingException(HttpStatus.CLIENT_ERROR_400_BAD_REQUEST);
            }
            String requestMethod = requestLineComponents[0];
            String requestResource = requestLineComponents[1];
            String requestProtocol = requestLineComponents[2];
            requestMap.put("method", requestMethod);
            requestMap.put("resource", requestResource);
            requestMap.put("protocol", requestProtocol);
        } catch (Exception e) {
            throw new HttpParsingException(HttpStatus.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    private void parseHeaders(InputStream inputStream, HashMap<String, String> requestMap) throws IOException, HttpParsingException {
        String headerLine = lineFromInputStream(inputStream);
        while (!headerLine.isEmpty()) {

            processSingleHeaderField(headerLine, requestMap);

            headerLine = lineFromInputStream(inputStream);
        }
    }

    private void processSingleHeaderField(String line , HashMap<String, String> requestMap) throws HttpParsingException {

        // found this regex use online
        Pattern pattern = Pattern.compile("^(?<fieldName>[!#$%&’*+\\-./^_‘|˜\\dA-Za-z]+):\\s?(?<fieldValue>[!#$%&’*+\\-./^_‘|˜(),:;<=>?@[\\\\]{}\" \\dA-Za-z]+)\\s?$");

        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            // We found a proper header
            String fieldName = matcher.group("fieldName").toLowerCase();
            String fieldValue = matcher.group("fieldValue");
            requestMap.put(fieldName, fieldValue);
        } else{
            throw new HttpParsingException(HttpStatus.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    private byte[] parseBody(InputStream inputStream, HashMap<String, String> requestMap) throws IOException {


            byte[] bodyBytes = null;
            // next comes the body (raw byte input stream to handle different types of data like images or forms)
            String contentLengthString = requestMap.get("content-length");
            if (contentLengthString != null) {
                int contentLength = Integer.parseInt(contentLengthString);
                bodyBytes = new byte[contentLength];

                int alreadyReadBytesAmount = 0;
                while (alreadyReadBytesAmount < contentLength) {
                    System.out.println(alreadyReadBytesAmount);
                    int readThisTime = inputStream.read(bodyBytes, alreadyReadBytesAmount, contentLength - alreadyReadBytesAmount);

                    if (readThisTime == -1) {
                        break;
                    }
                    alreadyReadBytesAmount += readThisTime;
                }

            }
            return bodyBytes;


    }
    private String lineFromInputStream(InputStream inputStream) throws IOException, HttpParsingException {

        // Don't know what would be good, but this is to prevent clients from sending an infinitely long line
        int maxLength = 500;


        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int byteRead;
        int totalBytes = 0;
        while ((byteRead = inputStream.read()) != -1) {
            if (byteRead == CR) {
                int nextRead = inputStream.read();
                if (nextRead == LF) {
                    return buffer.toString(StandardCharsets.US_ASCII);
                }
                throw new HttpParsingException(HttpStatus.CLIENT_ERROR_400_BAD_REQUEST);
            }
            if (byteRead == LF) {
                throw new HttpParsingException(HttpStatus.CLIENT_ERROR_400_BAD_REQUEST);
            }
            if (++totalBytes > maxLength) {
                throw new HttpParsingException(HttpStatus.CLIENT_ERROR_414_BAD_REQUEST);
            }
            buffer.write(byteRead);
        }
        if (totalBytes == 0) return "";
        throw new HttpParsingException(HttpStatus.CLIENT_ERROR_400_BAD_REQUEST);

    }
}
