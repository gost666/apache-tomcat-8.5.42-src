package testNetty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.List;

public class ByteLongDecoder extends ByteToMessageDecoder {
    private static final Log log = LogFactory.getLog(ByteLongDecoder.class);
    /**
     * decode():会根据接收的数据,被调用多次,直到确定没有新的元素被添加到list或者是ByteBuf没有更多的可读字节为止
     *  如果 List<Object> out 不为空,就会将list的内容传递给下一个  ChannelInboundHandler处理,该处理器的方法也会被调用多次
     * @param ctx:上下文对象
     * @param in:入站的ByteBuf
     * @param out:list集合,将解码后的数据传给下一个handler
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //因为Long为8个字节,需要判断有8个字节,才能读取一个long
        log.info("ByteLongDecoder decode被调用.....");
        if (in.readableBytes() >= 8) {
            out.add(in.readLong());
        }
    }
}
