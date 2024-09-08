package testNetty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.junit.Test;


public class nettyClient {
    private final Log log = LogFactory.getLog(nettyClient.class);
    @Test
    public void testNettyClient() throws Exception{
        EventLoopGroup eventExecutors = new NioEventLoopGroup();//客户端需要一个事件循环组
        try {
            Bootstrap bootstrap = new Bootstrap();//创建客户端启动对象;注意客户端使用的不是ServerBootstrap而是Bootstrap
            bootstrap.group(eventExecutors)//设置线程组
                    .channel(NioSocketChannel.class)//设置客户端通道的实现类(反射)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandler());//加入自己的处理器
                        }
                    });
            log.info("客户端准备好了....");
            //客户端连接服务端;ChannelFuture涉及netty异步模型
            ChannelFuture channelFuture = bootstrap.connect("192.168.31.199", 6668).sync();
            channelFuture.channel().closeFuture().sync();//给关闭通道进行监听
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }
}
