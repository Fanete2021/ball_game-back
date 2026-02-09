package com.game.handler.impl;

import com.game.dto.LeaderboardEntryDTO;
import com.game.dto.LeaderboardResponseDTO;
import com.game.repository.ScoreRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

public class LeaderboardHandler extends BaseHttpHandler {
    private final ScoreRepository scoreRepository;

    public LeaderboardHandler(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            QueryStringDecoder queryDecoder = new QueryStringDecoder(request.uri());
            Map<String, List<String>> params = queryDecoder.parameters();

            int limit = extractLimit(params);

            List<LeaderboardEntryDTO> topPlayers = scoreRepository.getTopPlayers(limit);
            LeaderboardResponseDTO response = new LeaderboardResponseDTO(topPlayers);

            sendJsonResponse(ctx, HttpResponseStatus.OK, response);

        } catch (Exception e) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Ошибка получения доски лидеров");
        }
    }

    private int extractLimit(Map<String, List<String>> params) {
        String limitStr = params.getOrDefault("limit", List.of("10")).get(0);
        try {
            return Integer.parseInt(limitStr);
        } catch (NumberFormatException e) {
            return 10;
        }
    }
}