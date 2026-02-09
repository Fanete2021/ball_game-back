package com.game.handler.impl;

import com.game.dto.ScoreRequestDTO;
import com.game.dto.ScoreResponseDTO;
import com.game.handler.BadRequestException;
import com.game.repository.ScoreRepository;
import com.game.repository.SessionRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class ScoreHandler extends BaseHttpHandler {
    private final SessionRepository sessionRepository;
    private final ScoreRepository scoreRepository;

    public ScoreHandler(SessionRepository sessionRepository, ScoreRepository scoreRepository) {
        this.sessionRepository = sessionRepository;
        this.scoreRepository = scoreRepository;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            ScoreRequestDTO scoreRequest = parseJson(request, ScoreRequestDTO.class);
            validateScoreRequest(scoreRequest);

            String nickname = sessionRepository.getNicknameBySessionId(scoreRequest.getSessionId());
            if (nickname == null) {
                sendError(ctx, HttpResponseStatus.BAD_REQUEST, "Такой никнейм не зарегистрирован в системе");
                return;
            }

            long sessionStartTime = sessionRepository.getSessionStartTime(scoreRequest.getSessionId());
            if (sessionStartTime == 0) {
                sessionStartTime = System.currentTimeMillis() / 1000;
            }

            long currentTime = System.currentTimeMillis() / 1000;

            sessionRepository.finishSession(scoreRequest.getSessionId());

            scoreRepository.saveScore(scoreRequest.getSessionId(), scoreRequest.getScore());

            int bestScore = scoreRepository.getBestScore(nickname);
            int globalRank = scoreRepository.getGlobalRank(nickname, scoreRequest.getScore(), currentTime);

            ScoreResponseDTO response = new ScoreResponseDTO(globalRank, bestScore);
            sendJsonResponse(ctx, HttpResponseStatus.OK, response);
        } catch (Exception e) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Ошибка при сохранении счёта");
        }
    }

    private void validateScoreRequest(ScoreRequestDTO request) throws BadRequestException {
        if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
            throw new BadRequestException("sessionId обязателен");
        }
        if (request.getScore() < 0) {
            throw new BadRequestException("score не может быть отрицателен");
        }
    }
}