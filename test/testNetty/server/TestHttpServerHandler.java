package testNetty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.net.URI;

/**
 * SimpleChannelInboundHandler是ChannelInboundHandlerAdapter的子类
 * HttpObject:客户端和服务器相互通讯的数据被封装成HttpObject
 */
public class TestHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final Log log = LogFactory.getLog(TestHttpServerHandler.class);

    /**
     * 读取客户端的数据
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        //判断msg是否是httpRequest请求
        if (msg instanceof HttpRequest) {
            log.info("msg类型:" + msg.getClass() + ";客户端地址:" + ctx.channel().remoteAddress());
            log.info("pipeLine hashCode:" + ctx.pipeline().hashCode() + ";TestHttpServerHandler hashCode:" + this.hashCode());
            //
            HttpRequest httpRequest = (HttpRequest) msg;
            //获取uri,过虑特定资源
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                log.info("请求了 favicon.ico,不做响应");
                return;
            }
            //回复信息给浏览器(http协议)
            ByteBuf content = Unpooled.copiedBuffer("hello,I'm a server", CharsetUtil.UTF_8);
            //构建一个http的响应,即httpResponse
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            //将构建好的response返回
            ctx.writeAndFlush(response);
        }
    }

}
