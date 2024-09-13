package testNetty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.List;

/**
 * 简化 ByteLongDecoder
 */
public class ByteLongDecoderSimple extends ReplayingDecoder<Void> {
    private static final Log log = LogFactory.getLog(ByteLongDecoderSimple.class);

    /**
     * 原有写法
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    //@Override
    //protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    //    //因为Long为8个字节,需要判断有8个字节,才能读取一个long
    //    log.info("ByteLongDecoder decode被调用.....");
    //    if (in.readableBytes() >= 8) {
    //        out.add(in.readLong());
    //    }
    //}

    /**
     * 简化后的写法
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("ByteLongDecoderSimple decode被调用.....");
        //ReplayingDecoder不需要判断数据是否足够读取,内部会进行处理判断
        //if (in.readableBytes() >= 8) {
        //    out.add(in.readLong());
        //}
        out.add(in.readLong());
    }

    /**
     * 其它常用解码器
     * LineBasedFrameDecoder:在Netty内部也有使用,它使用行尾控制符(\n或者 \r\n)作为分隔符来解析数据
     * DelimiterBasedFrameDecoder:w使用自定义的特殊字符作为消息的分隔符
     * HttpObjectDecoder:一个HTTP数据的解码器
     * LengthFieldBasedFrameDecoder:通过指定长度来标识整包消息,这样就可以自动的处理黏包和半包消息
     */
}
