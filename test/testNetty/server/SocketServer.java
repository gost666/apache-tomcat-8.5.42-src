package testNetty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class SocketServer {
    private static final Log log = LogFactory.getLog(SocketServer.class);

    /**
     * 通过WebSocket实现服务器和客户端长连接
     * 要求:
     * 1.实现基于WebSocket的长连接的全双工的交互
     * 2.改变Http协议多次请求的约束,实现长连接了,服务器可以发送消息给浏览器
     * 3.客户端和服务器关闭了,服务器会感知;同样浏览器关闭了,服务器会感知
     */
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder", new HttpServerCodec());
                            pipeline.addLast(new ChunkedWriteHandler());
                            /**
                             * HTTP数据在传输过程中是分段的,HttpObjectAggregator可以将多个段聚合
                             * 当浏览器发送大量数据时,就会发出多次Http请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            /**
                             * 1.对于webSocket,它的数据是以帧(frame)形式传递
                             * 2.WebSocketFrame下面有六个子类
                             * 3.浏览器请求时 ws://localhost:7000/hello 表示请求的uri
                             * 4.WebSocketServerProtocolHandler 核心功能是将http协议升级为 ws 协议(通过一个状态码101),保持长连接
                             *
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                            pipeline.addLast(new SocketServerHandler());
                        }
                    });
            log.info("Netty server begin....");
            ChannelFuture channelFuture = bootstrap.bind(7000).sync();
            //监听关闭
            channelFuture.channel().closeFuture().sync();
        } finally {

        }
    }
}
