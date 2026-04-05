
public enum HttpStatus {

    // Found most of this code online

    /* --- CLIENT ERRORS --- */
    CLIENT_ERROR_400_BAD_REQUEST(400, "Bad Request"),
    CLIENT_ERROR_404_NOT_FOUND(404, "Not Found" ),
    CLIENT_ERROR_405_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CLIENT_ERROR_414_BAD_REQUEST(414, "URI Too Long"),


    /* --- SERVER ERRORS --- */
    SERVER_ERROR_500_INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVER_ERROR_501_NOT_IMPLEMENTED(501, "Not Implemented"),
    SERVER_ERROR_505_HTTP_VERSION_NOT_SUPPORTED(505, "Http Version Not Supported"),

    OK(200,"OK" );


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
