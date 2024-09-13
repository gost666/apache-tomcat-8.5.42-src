package testNetty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import testNetty.server.ByteLongDecoder;
import testNetty.server.ByteLongDecoderSimple;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //加入出站的handler,对数据进行一个编码
        pipeline.addLast(new ByteLongEncoder());
        //pipeline.addLast(new ByteLongDecoder());//这是一个入站的解码器(入站Handler)
        pipeline.addLast(new ByteLongDecoderSimple());
        pipeline.addLast(new ClientHandler());
    }
}
