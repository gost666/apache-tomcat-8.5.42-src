package testNetty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.junit.Test;

import java.nio.charset.Charset;


public class nettyServer {
    private final Log log = LogFactory.getLog(nettyServer.class);
    /**
     * >>>>>>Netty原理<<<<<<<   观察者模式、职责链模式、命令模式
     * 1.Netty抽象出两组线程池:
     *  BossGroup:专门负责接收客户端的连接
     *  WorkerGroup:专门负责网络的读写
     * 2.BossGroup和WorkerGroup类型都是NioEventLoopGroup
     * 3.NioEventLoopGroup相当于一个事件循环组,包含有多个事件循环,每个事件循环是NioEventLoop
     *  每个NioEventLoop中包含一个Selector,一个taskQueue
     *  每个NioEventLoop的Selector上可以注册监听多个NioChannel
     *  每个NioChannel只会绑定在唯一的NioEventLoop上
     *  每个NioChannel都绑定有一个自己的ChannelPipeline
     * 4.NioEventLoop表示一个不断循环执行的执行处理任务的线程,每个NioEventLoop都有一个Selector,用于监听绑定在其上的socket的网络通讯
     * 5.NioEventLoopGroup可以有多个线程,即可以包含有多个NioEventLoop
     * 6.每个BossNioEventLoop循环执行的步骤有三步:
     *  a.轮询accept事件
     *  b.处理accept事件,与client建立连接,生成NioSocketChannel,并将其注册到某个workerNioEventLoop上的selector
     *  c.处理任务队列的任务,即runAllTasks
     * 7.每个WorkerNioEventLoop循环执行的步骤:
     *  a.轮询read和write事件
     *  b.处理i/o事件,即read和write事件,在对应NioSocketChannel处理
     *  c.处理任务队列的任务,即runAllTasks
     * 8.每个WorkerNioEventLoop处理业务时,会使用pipeline(管道),pipeline包含channel,即通过pipeline可以获取对应通道,管道中维护了很多的处理器
     *
     * 异步模型:
     * 1.异步的概念和同步的相对,当一个异步过程调用发出后,调用者不能立刻得到结果,实际处理这个 调用有组件在完成后;通过状态,通知,和回调来通知调用者
     * 2.Netty中的I/O操作都是异步的;包括Bind,Write,Connect等操作会简单的返回一个ChannelFuture
     * 3.调用者并不能立刻获得结果,而是通过Future-Listener机制,用户可以方便的主动获取或者通过通知机制获得操作结果
     * 4.Netty的异步模型是建立在future和callback之上的;callback就是回调;Futures核心:假设一个方法,计算过程可能非常耗时,等待方法返回显然不合适,
     *  可以在调用方法的时候立马返回一个Future,后续可以通过Future去监控方法的处理过程
     *
     * >>>>>>Netty核心模块<<<<<<<
     * Bootstrap:客户端启动引导类
     * ServerBootstrap:服务端启动引导类
     */

    @Test
    public void testNettyServer() throws Exception {
        //创建bossGroup和workerGroup
        //BossGroup专门负责接收客户端的连接,WorkerGroup专门负责网络的读写
        //两个都是无限循环
        //bossGroup和workerGroup含有的子线程(NioEventLoopGroup)的个数默认实际是(cpu*2)个
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();//创建服务器端的启动对象,配置参数
            bootstrap.group(bossGroup, workerGroup)//设置两个线程组
                    .channel(NioServerSocketChannel.class)//使用NioServerSocketChannel作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128)//设置线程队列得到连接个数;用来给ServerChannel添加配置
                    .handler(null)//该handler在bossGroup生效
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//设置保持活动连接的状态;用来给接收到的通道添加配置
                    //.handler(null)//该handler对应的是bossGroup,childHandle对应的是workerGroup
                    .childHandler(new ChannelInitializer<SocketChannel>() {//创建一个通道初始化对象(匿名对象);设置业务处理类
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {//该handler在workerGroup生效
                            /**
                             * 可以使用一个集合管理SocketChannel,再推送消息时,可以将业务加入到各个channel对应的NIOEventLoop的taskQueue或者scheduleTaskQueue
                             */
                            log.info("客户socketChannel hasCode:" + ch.hashCode());
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });//给我们的workerGroup的EventLoop对应的管道设置处理器
            log.info("...... Server is ready......");
            ChannelFuture channelFuture = bootstrap.bind(6668).sync();//绑定端口并且同步,生成一个ChannelFuture对象;启动服务器(并绑定端口)
            /**
             * 给channelFuture注册监听器
             */
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("监听端口6668成功");
                    } else {
                        System.out.println("监听端口6668失败");
                    }
                }
            });
            channelFuture.channel().closeFuture().sync();//对关闭通道进行监听
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }




    //方法作用见NettyServer.java
    //public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup):用于服务器端,用来设置两个EventLoopGroup
    //public ServerBootstrap group(EventLoopGroup group):用于客户端,用来设置一个EventLoopGroup
    //public B channel(Class<? extends C> channelClass):
    //public <T> B option(ChannelOption<T> option, T value):
    //public <T> ServerBootstrap childOption(ChannelOption<T> childOption, T value):
    //public ServerBootstrap childHandler(ChannelHandler childHandler):
    //public ChannelFuture bind(String inetHost, int inetPort):

        /**
         * Future和ChannelFuture常用方法:
         * 1.Channel channel():返回当前正在进行IO操作的通道
         * 2.ChannelFuture sync():等待异步操作执行完毕
         *  Future表示异步执行的结果,可以通过它提供的方法来检测执行是否完成,比如检索计算等
         * 3.ChannelFuture是一个接口,public interface ChannelFuture extends Future<Void> ;可以添加监听器,当监听的事件发生时,就会通知到监听器
         */

    //Channel:核心组件
    //Netty网络通信的组件,能够用于执行网络I/O操作;
    //可获得当前网络连接的通道的状态;
    //可获得网络连接的配置参数(如缓冲区大小);
    //提供异步的网络I/O操作(如连接,读写,绑定端口),异步调用意味任何I/O调用立即返回,并且不保证在调用结束时所请求的I/O操作已完成;
    //调用立即返回一个ChannelFuture实例,通过注册监听器到ChannelFuture上,可以I/O操作成功,失败或取消时回调通知消费方
    //支持关联I/O操作与对应的处理程序;
    //不同的协议,不同的阻塞类型的连接都有不同的Channel类型与之对应,常用的Channel类型:
    //NioSocketChannel:异步的客户端TCP Socket连接
    //NioServerSocketChannel:异步的服务器端TCP Socket连接
    //NioDatagramChannel:异步的UDP连接
    //NioSctpChannel:异步客户端Sctp连接
    //NioSctpServerChannel:异步服务端Sctp连接
    //        这些通道包含了UDP和TCP网络IO以及文件IO;

    //Selector:
    //Netty基于Selector对象实现IO多路复用,通过Selector一个线程可以监听多个连接的Channel事件;
    //当向一个Selector中注册的Channel是否有已就绪的IO事件(可读,可写,网络连接完成),这样程序就可以很简单地使用一个线程高效地管理多个Channel;

    //ChannelHandler:
    //是一个接口,处理IO事件或拦截IO操作,并将其转发到其ChannelPipeline(业务处理链)中的下一个处理程序
    //ChannelHandler本身并没有提供很多方法,因为这个接口有许多方法需要实现,方便使用期间可以继承它的子类

    //Pipeline和ChannelPipeline:重点
    //ChannelPipeline是一个Handler集合,它负责处理和拦截inbound或者outbound事件和操作,相当于一个贯穿Netty的链(ChannelPipeline是保存ChannelHandler的list,用于处理或拦截Channel的入站事件和出站操作)
    //ChannelPipeline实现了一种高级形式的拦截过滤器模式,使用户可以完全控制事件的处理方式,以及Channel中各个ChannelHandler如何相互交互
    //在Netty中每个Channel都有一个且仅有一个ChannelPipeline与之对应,关系如下:
    //一个Channel包含了一个ChannelPipeline,而ChannelPipeline中又维护了一个由ChannelHandlerContext组成的双向链表,并且每个ChannelHandlerContext中又关联着一个ChannelHandler.
    //        入站事件和出站事件在一个双向链表中,入站事件会从链表head往后传递到最后一个入站的handler,出站事件会从链表tall往前传递最最前一个出站的handler，两种类型的handle互不干扰

    //ChannelHandlerContext:
    //保存了Channel相关的所有上下文信息,同时关联一个ChannelHandler对象;
    //即ChannelHandlerContext中包含一个具体的事件处理器ChannelHandler,同时ChannelHandlerContext中也绑定了对应的Pipeline和Channel的信息,方便对ChannelHandler进行调用;
    //ChannelFuture close():关闭通道
    //ChannelOutboundInvoker flush():刷新
    //ChannelFuture writeAndFlush(Object msg):将数据写到ChannelPipeline中当前ChannelHandler的下一个ChannelHandler开始处理(出站)

    //ChannelOption:
    //Netty在创建Channel实例后,一般都需要设置ChannelOption参数

    //EventLoopGroup和NioEventLoopGroup:
    //NioEventLoopGroup是一组EventLoop的抽象,Netty为了更好地利用多核CPU资源,一般会有多个EventLoop同时工作,每个EventLoop维护一个Selector实例;
    //NioEventLoopGroup提供了next接口,可以从组里面按照一定规则获取其中一个EventLoop来处理任务,在Netty服务器编程中,一般都是需要提供两个EventLoopGroup,如BossEventLoopGroup和WorkerEventLoopGroup
     //       通常一个服务端口即一个ServerSocketChannel对应一个Selector和一个EventLoop线程负责接收客户端连接并将SocketChannel交给WorkerEventLoopGroup进行IO处理


    /**
     * Unpooled类:Netty提供一个专门用来操作缓冲区(Netty的数据容器)的工具类
     */
    @Test
    public void testUnpooled() {
        //1.创建对象,该对象包含一个数组arr,是一个byte[10]
        //2.在Netty中,不需要使用flip()进行反转;底层维护了readIndex和writeIndex
        ByteBuf buffer = Unpooled.buffer(10);
        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }
        log.info("capacity=" + buffer.capacity());
        //输出方法一
        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.getByte(i));
        }
        //输出方法二
        //for (int i = 0; i < buffer.capacity(); i++) {
        //    System.out.println(buffer.readByte());
        //}
        log.info("执行完毕....");
    }

    @Test
    public void testUnpooled2() {
        ByteBuf buffer = Unpooled.copiedBuffer("hello,world!", Charset.forName("utf8"));
        if (buffer.hasArray()) {
            byte[] content = buffer.array();
            System.out.println(new String(content, Charset.forName("utf-8")));
            System.out.println("buffer.arrayOffset():" + buffer.arrayOffset());//0
            System.out.println("buffer.readerIndex():" + buffer.readerIndex());//0
            System.out.println("buffer.writerIndex():" + buffer.writerIndex());//12
            System.out.println("buffer.capacity():" + buffer.capacity());
            System.out.println("buffer.capacity():" + buffer.readableBytes());//可读的字节数
            //取出各个字节数
            for (int i = 0; i < buffer.readableBytes(); i++) {
                System.out.println((char) buffer.getByte(i));
            }
            //取出某一段
            System.out.println(buffer.getCharSequence(0, 4, Charset.forName("utf-8")));//hell
        }
    }
}


































