package testNetty.client;

import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {

    @Test
    public void testBioClient()throws Exception{
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",6666);
        if (!socketChannel.connect(inetSocketAddress)){
            while (!socketChannel.finishConnect()){
                System.out.println("因为连接需要时间,客户端不会阻塞,可以估其它事情");
            }
        }
        String str = "client connection....";
        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes(),0,str.length());
        socketChannel.write(byteBuffer);
        System.in.read();
    }
}
