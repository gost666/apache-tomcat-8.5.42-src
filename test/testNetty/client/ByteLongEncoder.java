package testNetty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;


public class ByteLongEncoder extends MessageToByteEncoder<Long> {
    private static final Log log = LogFactory.getLog(ByteLongEncoder.class);

    /**
     * 编码的方法
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {
        log.info("ByteLongEncoder encode调用.....");
        log.info("msg:" + msg);
        out.writeLong(msg);
    }
}
