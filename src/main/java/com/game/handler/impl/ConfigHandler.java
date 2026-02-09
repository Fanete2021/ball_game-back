package com.game.handler.impl;

import com.game.dto.GameConfigDTO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

public class ConfigHandler extends BaseHttpHandler {

    @Override
    public void handle(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            GameConfigDTO config = new GameConfigDTO();
            sendJsonResponse(ctx, HttpResponseStatus.OK, config);
        } catch (Exception e) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Ошибка получения конфига");
        }
    }
}