package testNetty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.concurrent.TimeUnit;


public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private final Log log = LogFactory.getLog(NettyServerHandler.class);

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //对要写入的数据进行编码
        //将数据写入缓冲并刷新
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端...1", CharsetUtil.UTF_8));
    }

    //处理异常,关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 实际读取数据(可以读取客户端发送的消息)
     *
     * @param ctx:上下文对象,含有管道pipeline,通道channel,地址
     * @param msg:客户端发送的数据,默认Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("server ThreadInfo:" + Thread.currentThread().getName());
        log.info("server ctx = " + ctx);
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = ctx.pipeline();
        log.info("channel:" + channel + ",pipeline:" + pipeline);
        //将msg转换成一个ByteBuffer(Netty提供)
        ByteBuf buf = (ByteBuf) msg;
        log.info("客户端发送消息是:" + buf.toString(CharsetUtil.UTF_8));
        log.info("客户端地址:" + ctx.channel().remoteAddress());
        /**
         * 如果有一个非常耗时的业务:异步执行=>提交到channel对应的NioEventLoop的taskQueue中
         * 解决方案:
         *  1.用户程序自定义的普通任务
         *  2.用户自定义定时任务scheduledTaskQueue
         *  3.非当前Reactor线程调用Channel的各种方法;例:在推送系统的业务线程里面,根据用户有标识,找到对应的Channel引用,然后调用Write类的方法向该用户推送消息,
         *      就会进入到这种场景,最终提交到任务队列中后异步消费
         */
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(10 * 1000);//假设此处耗时
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端...2", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                log.info("发生异常信息....." + e.getMessage());
            }
        });
        //此任务需要等30秒(10+20)才能执行,两个任务在同一个线程
        ctx.channel().eventLoop().execute(() -> {
            try {
                Thread.sleep(20 * 1000);//假设此处耗时
                ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端...3", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                log.info("发生异常信息....." + e.getMessage());
            }
        });

        //用户自定义定时任务,该任务提交到ScheduledTaskQueue中
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5 * 1000);//假设此处耗时
                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,客户端...4", CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    log.info("发生异常信息....." + e.getMessage());
                }
            }
        }, 5, TimeUnit.SECONDS);

        log.info("go on....");
    }
}
