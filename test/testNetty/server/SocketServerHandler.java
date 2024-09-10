package testNetty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.time.LocalDateTime;

/**
 * TextWebSocketFrame类型:表示一个文本帧(frame)
 */
public class SocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Log log = LogFactory.getLog(SocketServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("server 收到消息:" + msg.text());
        //回复消息
        ctx.channel().writeAndFlush(new TextWebSocketFrame("Server时间:" + LocalDateTime.now() + ";" + msg.text()));
    }

    /**
     * 当web客户端连接后,触发该方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //id:唯一的值,asLongText()返回一个唯一的值,asShortText():不唯一
        log.info("handlerAdded 被调用-----" + ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerRemoved 被调用" + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        ctx.close();
    }
}
