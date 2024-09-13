package testNetty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //加入出站的handler,对数据进行一个编码
        pipeline.addLast(new ByteLongEncoder());
        pipeline.addLast(new ClientHandler());
    }
}
