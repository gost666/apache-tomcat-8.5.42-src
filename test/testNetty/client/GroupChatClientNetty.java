package testNetty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import testNetty.server.GroupChatServerNettyHandler;

import java.util.Scanner;

public class GroupChatClientNetty {
    private static final Log log = LogFactory.getLog(GroupChatClientNetty.class);

    private final String host;
    private final int port;

    public GroupChatClientNetty(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run()throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast(new GroupChatClientNettyHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
            Channel channel = channelFuture.channel();
            log.info("------" + channel.localAddress() + "------");
            //客户端需要输入信息
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String msg = scanner.nextLine();
                log.info("通过channel发送到服务器");
                channel.writeAndFlush(msg+"\r\n");
            }
        } finally {
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args)throws Exception {
        new GroupChatClientNetty("127.0.0.1",7000).run();
    }
}
