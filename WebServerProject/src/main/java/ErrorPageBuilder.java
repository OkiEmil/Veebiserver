import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ErrorPageBuilder {
    private HttpStatus status;
    private String optionalMessage;
    private String httpVersion;

    public ErrorPageBuilder(HttpStatus status, String optionalMessage, String version) {
        this.status = status;
        this.optionalMessage = optionalMessage;
        httpVersion = version;
    }

    public ErrorPageBuilder(HttpStatus status, String version) {
        this.status = status;
        httpVersion = version;
        this.optionalMessage = "";
    }

    public Response buildResponseFromError() {
        if (status.STATUS_CODE < 400) { // client errors and server errors only
            throw new RuntimeException("You should not be here with that status");
        }

        byte[] body = generateBody();
        return new HttpResponseBuilder().setHttpVersion(httpVersion)
                .setStatus(status)
                .addHeader("Content-type", "text/html; charset=utf-8")
                .addHeader("Content-length", String.valueOf(body.length))
                .setBody(body)
                .build();

    }

    private byte[] generateBody() {

        String  html = "<html>\n\t<head> <title>" +
                status.STATUS_CODE + " " + status.MESSAGE +
                "</title>" +
                "\n\t<body style='text-align: center; padding-top: 50px;'>" +
                "\n\t\t<h1>Error: " + status.STATUS_CODE + "</h1>" +
                "\n\t\t<h2>Message: " + status.MESSAGE + "</h2>" +
                "\n\t\t<p>" + optionalMessage + "</p>" +
                "\n\t</body>\n</html>";

        return html.getBytes(StandardCharsets.UTF_8);
    }
}
