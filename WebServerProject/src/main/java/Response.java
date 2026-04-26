import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response extends HttpMessage {

    private final String CRLF = "\r\n";
    private HttpStatus httpStatus;
    private String httpVersion;
    private InputStream bodyStream;

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    Response() {
    }


    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public byte[] getResponseAsBytes() {
        StringBuilder stringFromResponse = new StringBuilder();
        stringFromResponse.append(httpVersion)
                .append(" ")
                .append(httpStatus.STATUS_CODE)
                .append(" ")
                .append(httpStatus.MESSAGE)
                .append(CRLF);

        for (String headerName : getHeaderNames()) {
            stringFromResponse.append(headerName)
                    .append(": ")
                    .append(getHeader(headerName))
                    .append(CRLF);
        }
        stringFromResponse.append(CRLF);

        byte[] responseBytes = stringFromResponse.toString().getBytes(StandardCharsets.UTF_8);

        if (getHeader("content-length").equals("0") || getMessageBody().length == 0) {
            return responseBytes;
        }

        // combining 2 byte-arrays
        byte[] responseWithBody = new byte[responseBytes.length + getMessageBody().length];
        ByteBuffer buffer = ByteBuffer.wrap(responseWithBody);
        buffer.put(responseBytes);
        buffer.put(getMessageBody());
        return buffer.array();

    }

    public void setInputStream(InputStream inputStream) {
        this.bodyStream = inputStream;
    }

    public InputStream getInputStream() {
        return this.bodyStream;
    }

}
