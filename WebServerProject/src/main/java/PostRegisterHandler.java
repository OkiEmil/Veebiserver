import Routing.Router;
import Routing.WebrootHandler;
import UserManagement.Users;

public class PostRegisterHandler extends RequestHandler{


    private Logger logger;

    public PostRegisterHandler(Router router) {
        super("/register",router);
        logger = new Logger("log.directory" + "/Registration");
    }

    @Override
    protected Response handleRequest(Request request, SessionManager sessionManager) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (Users.getInstance().addUser(username,password)) {
            logger.log("Successfully added user: " + username + ".", true);
            byte[] body = "register successful".getBytes();
            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request,sessionManager))
                    .addHeader("Content-type", "text/html")
                    .addHeader("Content-length", String.valueOf(body.length))
                    .setBody(body);

            return responseBuilder.build();
        }
        else { // registering failed
            logger.log("Failed to add user: " + username + ".",true);
            byte[] body = "register failed".getBytes();
            HttpResponseBuilder responseBuilder = new HttpResponseBuilder(super.handleRequest(request,sessionManager))
                    .addHeader("Content-type", "text/html")
                    .addHeader("Content-length", String.valueOf(body.length))
                    .setBody(body);

            return responseBuilder.build();
        }
    }
}
