import Routing.Route;
import Routing.Router;
import Routing.WebrootHandler;

import java.util.Arrays;
import java.util.List;

public class ServerRouter {
    private final Router router;
    private final SessionManager sessionManager;
    private final List<RequestHandler> handlers;

    public ServerRouter() {
        this.router = routerSetup();
        this.sessionManager = new SessionManager();
        this.handlers = loadRequestHandlers();
    }
    private Router routerSetup() {
        Router router = new Router();
        router.addRoute(new Route(
                "/public/importantimages",
                new WebrootHandler("webroot/public/importantimages","/public/importantimages"),
                1
        ));
        router.addRoute(new Route(
                "/public",
                new WebrootHandler("webroot/public","/public"),
                0
        ));
        router.addRoute(new Route(
                "/",
                new WebrootHandler("webroot","/"),
                2
        ));
        return router;
    }
    private List<RequestHandler> loadRequestHandlers() {
        return Arrays.asList(
                new GetRequestHandler(this.router),
                new HeadRequestHandler(this.router),
                new DownloadRequestHandler(this.router),
                new PostRequestHandler(this.router)
        );
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public List<RequestHandler> getHandlers() {
        return handlers;
    }
}
