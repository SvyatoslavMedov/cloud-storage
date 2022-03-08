package com.geekbrains.cloud.netty;

import com.geekbrains.cloud.netty.handler.CloudMessageHandler;
import com.geekbrains.cloud.netty.handler.StringEchoHandler;
import com.geekbrains.cloud.netty.service.UserNameService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedDeque;

@Slf4j
public class NettyEchoServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup auth  = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        UserNameService userNameService = new UserNameService();
        ConcurrentLinkedDeque<ChannelHandlerContext> users = new ConcurrentLinkedDeque<>();


        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(auth,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new CloudMessageHandler()
                            );

                        }
                    });
            ChannelFuture future = bootstrap.bind(8189).sync();
            log.debug("Server started...");
            future.channel().closeFuture().sync();      // block
        }finally {
            {
                auth.shutdownGracefully();
                worker.shutdownGracefully();
            }
        }
    }

}
