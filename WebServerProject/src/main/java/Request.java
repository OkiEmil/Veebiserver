import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.net.URLDecoder.decode;

public class Request extends HttpMessage {

    private String requestMethod;
    private String requestResource;
    private String requestProtocol;
    private SessionState sessionState;
    private Map<String,String> parameters;

    public Request(byte[] bodyBytes, HashMap<String, String> requestMap) {
        this.requestMethod = requestMap.get("method");
        this.requestResource = requestMap.get("resource");
        this.requestProtocol = requestMap.get("protocol");
        if (bodyBytes != null) {
            this.setMessageBody(bodyBytes);
        }
        this.setHeaders(requestMap);
        this.parameters=this.getParameters();
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

    /**
     * gets cookies, for example in the form of Cookie: sessionId=123456; HttpOnly; HttpOnly; SameSite=Lax; Path=/
     * @return map of the cookies like  {sessionid : 123456, samesite : Lax, path : /} (in this case only the first one is useful)
     */
    public Map<String,String> getCookies() {
        Map<String,String> cookies = new HashMap<>();
        String header = this.getHeader("cookie");
        if (header!=null) {
            String[] values = header.split("; ");
            for (String value : values) {
                String[] parts = value.split("=",2);
                if (parts.length==2) {
                    cookies.put(parts[0].toLowerCase(),parts[1]);
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

    /**
     * gets parameters, for example in the form of key1=value1&key2=value2 (Content-Type application/x-www-form-urlencoded)
     * @return map of the parameters like {key1 : value1, key2 : value2}
     */
    private Map<String,String> getParameters() {
        if (parameters!=null)return parameters; // cache as a private map
        parameters=new HashMap<>();
        String contentType=getHeader("Content-Type");
        if (contentType!=null && contentType.startsWith("application/x-www-form-urlencoded")) {
            parameters=this.parseForm();
        }
        return parameters;
    }
    private Map<String,String> parseForm() {
        String body = new String(this.getMessageBody());
        Map<String,String> map = new HashMap<>();
        if (body.isEmpty()) return map;
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyAndValue = pair.split("=",2);
            String key;
            String value;
            try {
                key=decode(keyAndValue[0], StandardCharsets.UTF_8);
            } catch (Exception e) {key=keyAndValue[0];} // might need logging
            try {
                value=decode(keyAndValue[1], StandardCharsets.UTF_8);
            } catch (Exception e) {value=keyAndValue[1];} // might need logging
            map.put(key.toLowerCase(),value);
        }
        return map;
    }
    public String getParameter(String key) {
        return this.parameters.get(key);
    }
}
