package Routing;

import java.util.LinkedHashMap;
import java.util.Map;

public class Router {
    private final Map<String, Route> routes = new LinkedHashMap<>();

    public Router() {
    }
    public void addRoute(Route route) {
        routes.put(route.getPathPrefix(),route);
    }
    public Route resolve(String path) {
        System.out.println("Resolving path: " + path);
        for (Map.Entry<String, Route> entry : routes.entrySet()) {
            String prefix = entry.getKey();
            System.out.println(prefix);
            if (path.equals(prefix) || path.startsWith(prefix + "/") || prefix.equals("/")) {
                System.out.println(prefix + " -> " + entry.getValue().getPathPrefix());
                return entry.getValue();
            }
        }
        return null;
    }
}
