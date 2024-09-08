package testNetty.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {
    private Selector selector;
    private ServerSocketChannel listenChannel;
    private static final int PORT = 6667;

    public GroupChatServer() {
        try {
            selector = Selector.open();
            listenChannel = ServerSocketChannel.open();
            listenChannel.socket().bind(new InetSocketAddress(PORT));
            listenChannel.configureBlocking(false);
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroupChatServer server = new GroupChatServer();
        server.listen();
    }

    public void listen() {
        while (true) {
            try {
                int count = selector.select();
                if (count > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        //取出SelectionKey
                        SelectionKey key = iterator.next();
                        //监听accept
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = listenChannel.accept();
                            socketChannel.configureBlocking(false);
                            //将该socketChannel注册到Selector
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            //上线提示
                            System.out.println(socketChannel.getRemoteAddress() + "上线了");
                        }
                        //监听发送read事件,即通道是可读的状态
                        if (key.isReadable()) {
                            readData(key);
                        }
                        //当前key删除,防止重复
                        iterator.remove();
                    }
                } else {
                    System.out.println("wait....");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readData(SelectionKey key) {
        SocketChannel channel = null;
        try {
            //取到关联的channel
            channel = (SocketChannel) key.channel();
            //创建缓冲区
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int count = channel.read(byteBuffer);
            if (count > 0) {
                //将缓冲区的数据转换成字符串
                String s = new String(byteBuffer.array());
                System.out.println("from client:" + s);
                //向其它客户端转发消息(排除自己)
                sendInfoToOtherClient(s, channel);
            }
        } catch (IOException e) {
            try {
                System.out.println(channel.getRemoteAddress() + "离线了");
                key.cancel();//取消注册
                channel.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    private void sendInfoToOtherClient(String s, SocketChannel channel) throws IOException {
        System.out.println("消息转发中");
        for (SelectionKey key : selector.keys()) {
            //通过key取出对应的SocketChannel
            Channel targetChannel = key.channel();
            //排除自己
            if (targetChannel instanceof SocketChannel && targetChannel != channel) {
                SocketChannel dest = (SocketChannel) targetChannel;
                //将s存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(s.getBytes());
                //将buffer的数据写入通道
                dest.write(buffer);
            }
        }
    }
}
