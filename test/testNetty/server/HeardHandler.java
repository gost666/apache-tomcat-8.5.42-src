package testNetty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class HeardHandler extends ChannelInboundHandlerAdapter {
    private static final Log log = LogFactory.getLog(HeardHandler.class);

    /**
     *
     * @param ctx:上下文
     * @param evt:事件
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = null;
            switch (event.state()){
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }
            log.info(ctx.channel().remoteAddress() + "超时事件:" + eventType);
            log.info("服务器做其它处理....");
        }
    }
}
