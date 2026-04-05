import java.util.HashMap;
import java.util.Set;

public abstract class HttpMessage {
    private HashMap<String, String> headers = new HashMap<>();
    private byte[] bodyBytes = new byte[0];

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public String getHeader(String headerName) {
        return headers.get(headerName.toLowerCase());
    }

    public void addHeader(String headerName, String headerField) {
        headers.put(headerName.toLowerCase(), headerField);
    }

    public byte[] getMessageBody() {
        return bodyBytes;
    }

    public void setMessageBody(byte[] messageBody) {
        this.bodyBytes = messageBody;
    }
}
