package testNetty.client;

import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

    @Test
    public void testOldClient() throws IOException {
        Socket socket = new Socket("localhost", 7001);
        String fileName = "";
        FileInputStream inputStream = new FileInputStream(fileName);
        DataOutputStream dataInputStream = new DataOutputStream(socket.getOutputStream());
        byte[] buffer = new byte[4096];
        long readCount;
        long total = 0;
        long starTime = System.currentTimeMillis();
        while ((readCount = inputStream.read(buffer)) > 0) {
            total += readCount;
            dataInputStream.write(buffer);
        }
        System.out.println("发送总字节数:" + total + ",耗时:" + (System.currentTimeMillis() - starTime));
        dataInputStream.close();
        socket.close();
        inputStream.close();
    }

    @Test
    public void testNewClient() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 7001));
        String fileName = "";
        FileChannel fileChannel = new FileInputStream(fileName).getChannel();
        long starTime = System.currentTimeMillis();
        //Linux下transferTo()就可以完成传输
        //Windows下一次调用transferTo()只能发送8M,就需要分段传输文件,而且要主要传输和位置
        long transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);
        System.out.println("发送总字节数:" + transferCount + ",耗时:" + (System.currentTimeMillis() - starTime));
        fileChannel.close();
    }

    @Test
    public void testSocketClient() {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            while (true) {
                String outMsg = bufferedReader.readLine();
                bufferedWriter.write(outMsg);
                bufferedWriter.write("\n");
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
