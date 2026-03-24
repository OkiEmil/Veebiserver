import java.util.Arrays;
import java.util.Map;

public class Request {

    private String requestMethod;
    private String requestResource;
    private String requestProtocol;
    private byte[] bodyBytes;
    private Map<String, String> headers;

    public Request(byte[] bodyBytes, Map<String, String> requestMap) {
        this.requestMethod = requestMap.get("Method");
        this.requestResource = requestMap.get("Resource");
        this.requestProtocol = requestMap.get("Protocol");
        this.bodyBytes = bodyBytes;
        this.headers = requestMap;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestMethod='" + requestMethod + '\'' +
                ", requestResource='" + requestResource + '\'' +
                ", requestProtocol='" + requestProtocol + '\'' +
                ", bodyBytes=" + new String(bodyBytes) +
                ", headers=" + headers +
                '}';
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestResource() {
        return requestResource;
    }

    public String getRequestProtocol() {
        return requestProtocol;
    }

    public byte[] getBodyBytes() {
        return bodyBytes;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
