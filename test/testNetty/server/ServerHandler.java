package testNetty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class ServerHandler extends SimpleChannelInboundHandler<Long> {
    private static final Log log = LogFactory.getLog(ServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        log.info("从客户端:"+ctx.channel().remoteAddress()+"读取到long:"+ msg);
        //给客户端发送一个long
        ctx.writeAndFlush(98765L);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
