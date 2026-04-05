import java.util.HashMap;

public class HttpResponseBuilder {
    private Response response;

    public HttpResponseBuilder() {
        this.response = new Response();
    }
    public HttpResponseBuilder(Response response) {this.response = response;}
    public HttpResponseBuilder setStatus(HttpStatus status) {
        this.response.setHttpStatus(status);
        return this;
    }
    public HttpResponseBuilder setHttpVersion(String version) {
        this.response.setHttpVersion(version);
        return this;
    }
    public HttpResponseBuilder setBody(byte[] body) {
        this.response.setMessageBody(body);
        return this.addHeader("content-length", String.valueOf(body.length));
    }
    public HttpResponseBuilder addHeader(String headerName, String headerField) {
        this.response.addHeader(headerName, headerField);
        return this;
    }
    public HttpResponseBuilder setHeaders(HashMap<String, String> headers) {
        this.response.setHeaders(headers);
        return this;
    }
    public Response build() {
        if (response.getHeader("content-length") == null) {
            addHeader("content-length", "0");
        }
        return response;
    }

}
