package com.game.handler.impl;

import com.game.dto.SessionStartRequest;
import com.game.dto.SessionStartResponse;
import com.game.handler.BadRequestException;
import com.game.repository.SessionRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class SessionHandler extends BaseHttpHandler {
    private final SessionRepository sessionRepository;

    public SessionHandler(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void handle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        try {
            SessionStartRequest sessionRequest = parseJson(request, SessionStartRequest.class);
            validateSessionRequest(sessionRequest);

            String sessionId = sessionRepository.createSession(sessionRequest.getNickname(), sessionRequest.getClientVersion());

            SessionStartResponse response = new SessionStartResponse(sessionId,System.currentTimeMillis() / 1000);

            sendJsonResponse(ctx, HttpResponseStatus.OK, response);
        } catch (Exception e) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Не удалось начать сессию");
        }
    }

    private void validateSessionRequest(SessionStartRequest request) throws BadRequestException {
        if (request.getNickname() == null || request.getNickname().trim().isEmpty()) {
            throw new BadRequestException("Никнейм обязателен");
        }
    }
}