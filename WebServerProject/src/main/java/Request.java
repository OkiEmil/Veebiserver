import java.util.HashMap;
import java.util.Map;

public class Request extends HttpMessage {

    private String requestMethod;
    private String requestResource;
    private String requestProtocol;
    private SessionState sessionState;

    public Request(byte[] bodyBytes, HashMap<String, String> requestMap) {
        this.requestMethod = requestMap.get("method");
        this.requestResource = requestMap.get("resource");
        this.requestProtocol = requestMap.get("protocol");
        if (bodyBytes != null) {
            this.setMessageBody(bodyBytes);
        }
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

    public Map<String,String> getCookies() {
        Map<String,String> cookies = new HashMap<>();
        String header = this.getHeader("cookie");
        if (header!=null) {
            String[] values = header.split("; ");
            for (String value : values) {
                String[] parts = value.split("=",2);
                if (parts.length==2) {
                    cookies.put(parts[0],parts[1]);
                }
            }
        }
        return cookies;
    }

    public SessionState getSessionState() {
        return sessionState;
    }

    public void setSessionState(SessionState sessionState) {
        this.sessionState=sessionState;
    }
    // TODO add params method (might actually be in PostRequestHandler)
}
