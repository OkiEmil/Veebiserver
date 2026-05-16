
public enum HttpStatus {

    // Found most of this code online

    /* --- CLIENT ERRORS --- */
    CLIENT_ERROR_400_BAD_REQUEST(400, "Bad Request"),
    CLIENT_ERROR_403_FORBIDDEN(403, "Forbidden"),
    CLIENT_ERROR_404_NOT_FOUND(404, "Not Found" ),
    CLIENT_ERROR_405_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CLIENT_ERROR_413_PAYLOAD_TOO_LARGE(414, "Request body too large"),
    CLIENT_ERROR_414_BAD_REQUEST(414, "URI Too Long"),
    CLIENT_ERROR_415_UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    CLIENT_ERROR_429_TOO_MANY_REQUESTS(429, "Too many requests"),
    CLIENT_ERROR_431_TOO_MANY_HEADERS(431, "Request Header Fields Too Large"),

    /* --- SERVER ERRORS --- */
    SERVER_ERROR_500_INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVER_ERROR_501_NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED(505, "Http Version Not Supported"),

    OK(200,"OK" ),
    CREATED(201, "CREATED");

    public final int STATUS_CODE;
    public final String MESSAGE;

    HttpStatus(int STATUS_CODE, String MESSAGE) {
        this.STATUS_CODE = STATUS_CODE;
        this.MESSAGE = MESSAGE;
    }
    public HttpStatus getByCode(int statusCode) {
        for (HttpStatus status : HttpStatus.values()) {
            if (status.STATUS_CODE == statusCode) return status;
        }
        return SERVER_ERROR_500_INTERNAL_SERVER_ERROR; // if no error with that code was found
    }
}
