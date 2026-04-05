public class HttpParsingException extends Exception{
    private HttpStatus errorCode;

    public HttpParsingException(HttpStatus errorCode) {
        super(errorCode.MESSAGE);
        this.errorCode = errorCode;
    }

    public HttpStatus getErrorCode() {
        return errorCode;
    }
}
