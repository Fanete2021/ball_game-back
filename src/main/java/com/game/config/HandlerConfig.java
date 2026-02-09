package com.game.config;

import com.game.handler.routing.RequestRouter;
import com.game.handler.impl.*;
import com.game.repository.ScoreRepository;
import com.game.repository.SessionRepository;
import com.game.service.TelemetryService;
import io.netty.handler.codec.http.HttpMethod;

public class HandlerConfig {

    public static RequestRouter createRouter() {
        RequestRouter router = new RequestRouter();

        SessionRepository sessionRepo = new SessionRepository();
        TelemetryService telemetryService = new TelemetryService();
        ScoreRepository scoreRepo = new ScoreRepository();

        router.registerRoute(HttpMethod.GET, "/config", new ConfigHandler());
        router.registerRoute(HttpMethod.GET, "/leaderboard", new LeaderboardHandler(scoreRepo));
        router.registerRoute(HttpMethod.POST, "/session", new SessionHandler(sessionRepo));
        router.registerRoute(HttpMethod.POST, "/events", new TelemetryHandler(telemetryService));
        router.registerRoute(HttpMethod.POST, "/score", new ScoreHandler(sessionRepo, scoreRepo));

        return router;
    }
}