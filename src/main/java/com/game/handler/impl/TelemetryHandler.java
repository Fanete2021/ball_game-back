package com.game.handler.impl;

import com.game.dto.TelemetryRequestDTO;
import com.game.dto.TelemetryResponseDTO;
import com.game.handler.BadRequestException;
import com.game.service.TelemetryService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class TelemetryHandler extends BaseHttpHandler {
    private final TelemetryService telemetryService;
    private static final int MAX_EVENTS_PER_REQUEST = 500;

    public TelemetryHandler(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            TelemetryRequestDTO telemetryRequest = parseJson(request, TelemetryRequestDTO.class);
            validateTelemetryRequest(telemetryRequest);

            if (telemetryRequest.getEvents().size() > MAX_EVENTS_PER_REQUEST) {
                sendError(ctx, HttpResponseStatus.BAD_REQUEST,"Слишком много событий. Максимум: " + MAX_EVENTS_PER_REQUEST);
                return;
            }

            boolean success = telemetryService.saveTelemetry(telemetryRequest);

            if (success) {
                TelemetryResponseDTO response = new TelemetryResponseDTO(true);
                sendJsonResponse(ctx, HttpResponseStatus.OK, response);
            }
        } catch (Exception e) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Ошибка сохранения сессии");
        }
    }

    private void validateTelemetryRequest(TelemetryRequestDTO request) throws BadRequestException {
        if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
            throw new BadRequestException("sessionId обязателен");
        }
        if (request.getEvents() == null || request.getEvents().isEmpty()) {
            throw new BadRequestException("events пустой");
        }
    }
}