package testNetty.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final Log log = LogFactory.getLog(NettyClientHandler.class);
    /**
     * 当通道就绪就会触发该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Client" + ctx);
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,server:", CharsetUtil.UTF_8));
    }

    /**
     * 当通道有读取事件时,会触发
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf =(ByteBuf) msg;
        log.info("服务器回复的消息:" + buf.toString(CharsetUtil.UTF_8) + ";服务器端地址:" + ctx.channel().remoteAddress());
    }

    /**
     * 异常触发
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
