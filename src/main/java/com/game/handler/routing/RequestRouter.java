package com.game.handler.routing;

import com.game.handler.RequestHandler;
import io.netty.handler.codec.http.HttpMethod;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class RequestRouter {
    private final Map<RouteKey, RequestHandler> routeHandlers = new HashMap<>();

    public record RouteKey(HttpMethod method, String path) {}

    public void registerRoute(HttpMethod method, String path, RequestHandler handler) {
        routeHandlers.put(new RouteKey(method, path), handler);
    }

    public Optional<RequestHandler> findHandler(HttpMethod method, String uri) {
        for (Map.Entry<RouteKey, RequestHandler> entry : routeHandlers.entrySet()) {
            RouteKey key = entry.getKey();

            if (key.method().equals(method)) {
                if (key.path().equals(uri)) {
                    return Optional.of(entry.getValue());
                }
                if (key.path().equals("/leaderboard") && uri.startsWith("/leaderboard")) {
                    return Optional.of(entry.getValue());
                }
            }
        }
        return Optional.empty();
    }
}