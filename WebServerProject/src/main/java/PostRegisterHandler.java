public class PostRegisterHandler extends PostRequestHandler{

    public PostRegisterHandler(WebrootHandler webrootHandler) {
        super(webrootHandler);
    }

    @Override
    protected Response handleRequest(Request request) {
        return new Response();
    }
}
