package org.example.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServer {
    public static void main(String[] args) throws Exception {
        int port = 8080;
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            // HTTP 디코더 + 인코더
                            p.addLast(new HttpServerCodec());
                            // 요청을 전체로 모아주는 aggregator (옵션)
                            p.addLast(new HttpObjectAggregator(65536));
                            p.addLast(new HttpServerHandler());
                        }
                    });

            Channel ch = b.bind(port).sync().channel();
            log.info("HTTP 서버가 " + port + "번 포트에서 시작되었습니다.");
            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

