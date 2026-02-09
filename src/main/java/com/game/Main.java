package com.game;

import com.game.config.DatabaseConfig;
import com.game.config.HandlerConfig;
import com.game.handler.HttpServerHandler;
import com.game.handler.routing.RequestRouter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Main {
    private static final int PORT = 8080;
    private static final int MAX_CONTENT_LENGTH = 1048576;

    public void start() throws InterruptedException {
        RequestRouter router = HandlerConfig.createRouter();
        HttpServerHandler httpHandler = new HttpServerHandler(router);

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        CorsConfig corsConfig = CorsConfigBuilder.forOrigin("http://localhost:5173")
            .allowedRequestMethods(
                io.netty.handler.codec.http.HttpMethod.GET,
                io.netty.handler.codec.http.HttpMethod.POST,
                io.netty.handler.codec.http.HttpMethod.OPTIONS
            )
            .allowedRequestHeaders("Content-Type", "Accept", "Origin")
            .allowCredentials()
            .maxAge(86400) // 24 часа
            .build();


        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new HttpServerCodec())
                                    .addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH))
                                    .addLast(new CorsHandler(corsConfig))
                                    .addLast(httpHandler);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = bootstrap.bind(PORT).sync();
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseConfig::closeDataSource));

        Main server = new Main();
        try {
            server.start();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}