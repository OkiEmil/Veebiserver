import UserManagement.Users;

public class PostRegisterHandler extends PostRequestHandler{

    public PostRegisterHandler(WebrootHandler webrootHandler) {
        super(webrootHandler);
    }

    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (Users.getInstance().addUser(username,password)) {
            byte[] body = "register successful".getBytes();
            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request, sessionManager))
                    .addHeader("Content-type", "text/html")
                    .addHeader("Content-length", String.valueOf(body.length))
                    .setBody(body);

            return responseBuilder.build();
        }
        else { // registering failed
            byte[] body = "register failed".getBytes();
            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request, sessionManager))
                    .addHeader("Content-type", "text/html")
                    .addHeader("Content-length", String.valueOf(body.length))
                    .setBody(body);

            return responseBuilder.build();
        }
    }
}
