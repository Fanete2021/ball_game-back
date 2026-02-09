package com.game.handler;

import com.game.handler.routing.RequestRouter;
import com.game.utils.HttpResponseUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.Optional;

@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final RequestRouter router;

    public HttpServerHandler(RequestRouter router) {
        this.router = router;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        HttpMethod method = request.method();

        try {
            Optional<RequestHandler> handler = router.findHandler(method, uri);

            if (handler.isPresent()) {
                handler.get().handle(ctx, request);
            } else {
                sendError(ctx, HttpResponseStatus.NOT_FOUND, "Эндпоинт не найден");
            }
        } catch (Exception e) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
        }
    }

    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, String message) {
        ctx.writeAndFlush(HttpResponseUtils.createJsonResponse(status, status.code() + " " + status.reasonPhrase() + " " + message));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}