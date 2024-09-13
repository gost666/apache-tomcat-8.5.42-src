package testNetty.client;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class ClientHandler extends SimpleChannelInboundHandler<Long> {

    private final Log log = LogFactory.getLog(ClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        log.info("Server IP:" + ctx.channel().remoteAddress());
        log.info("收到Server message:" + msg);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("ClientHandler 发送数据");
        ctx.writeAndFlush(1234566L);

        /**
         * 分析:
         * abcdabcdabcd:是16个字节
         */
        //ctx.writeAndFlush(Unpooled.copiedBuffer("abcdabcdabcdabcd",CharsetUtil.UTF_8));
    }
}
