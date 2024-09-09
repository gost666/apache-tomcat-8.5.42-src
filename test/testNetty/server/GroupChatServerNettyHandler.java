package testNetty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Date;


public class GroupChatServerNettyHandler extends SimpleChannelInboundHandler<String> {

    private static final Log log = LogFactory.getLog(GroupChatServerNettyHandler.class);


    //定义一个handler组,管理所有的handler
    //GlobalEventExecutor.INSTANCE:是全局的事件唯一执行器,是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 当连接建立,第一个执行
     * 将当前Channel加入到channelGroup中
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户加入聊天的信息推送给其它在线的客户端
        /**
         * 该访求会将channelGroup中的所有channel遍历,并发送消息,不需要自己遍历
         */
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "加入聊天\n" + simpleDateFormat.format(new Date()) + "\n");
        channelGroup.add(channel);
    }

    /**
     * 表示断开连接,将 xxx客户离开信息推送给当前在线的客户
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "离开了\n");
        log.info("channelGroup size:" + channelGroup.size());
    }

    /**
     * 表示channel处理活动状态,提示 xxx 上线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress() + "上线了");
    }

    /**
     * 表示channel处理离线状态,提示 xxx 离线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info(ctx.channel().remoteAddress() + "下线了");
    }

    /**
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        //遍历channelGroup,根据不同的情况,回送不同的消息
        channelGroup.forEach(ch -> {
            if (channel != ch) {
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + "发送消息" + msg + "\n");
            } else {
                ch.writeAndFlush("自己发送了消息:" + msg + "\n");
            }
        });
    }

    /**
     * 发生异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
