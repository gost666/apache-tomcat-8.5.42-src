package testNetty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Netty心跳检测机制
 */
public class HeardServer {

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            /**
                             * IdleStateHandler:netty提供的空闲状态的处理器
                             * readerIdleTime:表示多长时间没有读,就会发送一个心跳检测包,检测是否连接状态
                             * writerIdleTime:表示多长时间没有写,就会发送一个心跳检测包,检测是否连接状态
                             * allIdleTime:表示多长时间没有读/写,就会发送一个心跳检测包,检测是否连接状态
                             *
                             * 当 Channel 有一段时间没有执行读、写或两者操作时触发 IdleStateEvent
                             * IdleStateHandler:触发后,就会传递给管道的下一个Handler去处理,通过调用下一个Handler的UserEventTiggered,在该方法中去处理
                             *  IdleStateHandler(读空闲/写空闲/读写空闲)
                             */
                            pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));
                            //增加一个对空闲检测进一步处理的Handler(自定义)
                            pipeline.addLast(new HeardHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(7000).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}