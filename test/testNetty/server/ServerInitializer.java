package testNetty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import testNetty.client.ByteLongEncoder;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ByteLongDecoder());
        pipeline.addLast(new ByteLongEncoder());//这是一个出站的编码器(出站Handler)
        pipeline.addLast(new ServerHandler());
    }
}
