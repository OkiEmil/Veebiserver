public class PostLoginHandler extends PostRequestHandler{

    public PostLoginHandler(WebrootHandler webrootHandler) {
        super(webrootHandler);

    }

    @Override
    protected Response handleRequest(Request request) {
        return new Response();
    }
}
