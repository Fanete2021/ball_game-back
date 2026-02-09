package com.game.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.handler.BadRequestException;
import com.game.handler.RequestHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class BaseHttpHandler implements RequestHandler {
    protected final ObjectMapper objectMapper = new ObjectMapper();
    protected <T> T parseJson(FullHttpRequest request, Class<T> clazz) throws BadRequestException {
        try {
            String json = request.content().toString(CharsetUtil.UTF_8);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new BadRequestException("Не конвертируется");
        }
    }

    protected void sendJsonResponse(ChannelHandlerContext ctx, HttpResponseStatus status, Object response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            ByteBuf content = Unpooled.copiedBuffer(json, StandardCharsets.UTF_8);

            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);

            httpResponse.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8")
                .set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes())
                .set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .set(HttpHeaderNames.CACHE_CONTROL, "no-cache");

            ctx.writeAndFlush(httpResponse);
        } catch (Exception e) {
            ctx.close();
        }
    }

    protected void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, String message) {
        try {
            String errorJson = objectMapper.writeValueAsString(
                Map.of("error", status.code() + " " + status.reasonPhrase(), "message", message)
            );
            sendJsonResponse(ctx, status, errorJson);
        } catch (Exception e) {
            ctx.close();
        }
    }
}