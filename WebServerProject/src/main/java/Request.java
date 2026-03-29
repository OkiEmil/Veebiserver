import java.util.HashMap;

public class Request extends HttpMessage {

    private String requestMethod;
    private String requestResource;
    private String requestProtocol;

    public Request(byte[] bodyBytes, HashMap<String, String> requestMap) {
        this.requestMethod = requestMap.get("Method");
        this.requestResource = requestMap.get("Resource");
        this.requestProtocol = requestMap.get("Protocol");
        this.setMessageBody(bodyBytes);
        this.setHeaders(requestMap);
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestMethod='" + requestMethod + '\'' +
                ", requestResource='" + requestResource + '\'' +
                ", requestProtocol='" + requestProtocol + '\'' +
                ", bodyBytes=" + new String(getMessageBody()) +
                ", headers=" + getHeaderNames() +
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

}
