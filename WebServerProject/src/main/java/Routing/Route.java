package Routing;

public class Route {
    private final String pathPrefix;
    private final WebrootHandler webrootHandler;
    private final int accessLevel;

    public Route(String pathPrefix, WebrootHandler webrootHandler, int accessLevel) {
        this.pathPrefix = removeLastSlash(pathPrefix);
        this.webrootHandler = webrootHandler;
        this.accessLevel = accessLevel;
    }

    public String getPathPrefix() {
        return this.pathPrefix;
    }

    public WebrootHandler getWebrootHandler() {
        return this.webrootHandler;
    }

    public int getAccessLevel() {
        return this.accessLevel;
    }
    private String removeLastSlash(String path) {

        if (path.endsWith("/") && path.length() > 1) {
            return path.substring(0, path.length() - 1);
        }

        return path;
    }
}
