package testNetty.server;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    /**
     * 1.每个Channel对应一个Buffer
     * 2.Selector对应一个线程,一个线程对应一个Channel(连接)
     * 3.程序切换到哪个Channel是由事件决定的,Event就是一个重要的概念
     * 4.Selector会根据不同的事件,在各个通道上切换
     * 5.Buffer是一个内存块,底层就是一个数组
     * 6.数据的读写是通过Buffer,BIO(要么是输入流,或者是输出流,不能是双向的),NIO(可以读也可以写,需要flip()切换)
     * 7.Channel是双向的,可以返回底层操作系统的情况;比如Linux,底层操作系统通道就是双向的
     */

    /**
     * 1.Channel可以同时进行读写,而流只能读或者只能写
     * 2.通道可以实现异步读写数据
     * 3.可以从缓冲Buffer读数据,也可以写数据到Buffer
     */
    @Test
    public void testNIOBuffer() {
        //创建一个Buffer,大小为5,即可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);
        //向buffer存放数据
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i + 2);
        }
        //将buffer转换,读写切换
        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            System.out.println(intBuffer.get());
        }
    }

    /**
     * 将字符串写到文件中
     */
    @Test
    public void testWriteFileChannel() throws Exception {
        String str = "Hello";
        //创建一个输出流(Channel)
        FileOutputStream outputStream = new FileOutputStream("F:\\testChannel.txt");
        FileChannel fileChannel = outputStream.getChannel();
        //创建一个缓冲区(ByteBuffer)
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //将str放入byteBuffer
        byteBuffer.put(str.getBytes());
        //对byteBuffer进行反转
        byteBuffer.flip();
        //将byteBuffer数据写入到fileChannel
        fileChannel.write(byteBuffer);
        outputStream.close();
    }

    /**
     * 从文件中读取数据
     */
    @Test
    public void testReadFileChannel() throws Exception {
        File file = new File("F:\\testChannel.txt");
        FileInputStream inputStream = new FileInputStream(file);
        FileChannel fileChannel = inputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
        fileChannel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array()));
        inputStream.close();
    }

    /**
     * 使用一个Channel将一个文件中的数据写到另一个文件中
     * 将testChannel.txt中的内容写入到2.txt中
     */
    @Test
    public void testOneChannelReadWrite() throws Exception {
        FileInputStream inputStream = new FileInputStream("F:\\testChannel.txt");
        FileChannel inChannel = inputStream.getChannel();

        FileOutputStream outputStream = new FileOutputStream("F:\\2.txt");
        FileChannel outChannel = outputStream.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        while (true) {
            byteBuffer.clear();//清空buffer,此处可以查看断点 position和limit的值
            int read = inChannel.read(byteBuffer);
            if (read == -1) {
                break;
            }
            byteBuffer.flip();
            outChannel.write(byteBuffer);
        }
        inChannel.close();
        outChannel.close();
    }

    /**
     * 通过transferFrom()实现文件的拷贝
     */
    @Test
    public void testTransfer() throws Exception {
        FileInputStream inputStream = new FileInputStream("F:\\07.png");
        FileChannel inChannel = inputStream.getChannel();

        FileOutputStream outputStream = new FileOutputStream("F:\\8.png");
        FileChannel outChannel = outputStream.getChannel();

        outChannel.transferFrom(inChannel, 0, inChannel.size());
        inChannel.close();
        outChannel.close();
    }

    /**
     * Buffer和Channel的注意事项
     * 1.ByteBuffer1支持类型化的put和get,put放入的是什么数据类型,get就应该使用相应的数据类型来取出,否则可能有BufferUnderflowException
     * 2.可以将一个普通Buffer转成只读Buffer
     * 3.NIO还提供了MappedByteBuffer,可以让文件直接在内存(堆外的内存)中进行修改,而如何同步到文件由NIO完成
     * 4.读写操作都是通过一个Buffer完成,NIO还支持通过多个Buffer完成读写操作.即Scattering和Gathering
     */
    @Test
    public void testPutAndGet(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);
        byteBuffer.putInt(100);
        byteBuffer.putLong(100);
        byteBuffer.putChar('1');
        byteBuffer.putShort((short) 100);
        byteBuffer.flip();
        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getLong());
        System.out.println(byteBuffer.getChar());
        System.out.println(byteBuffer.getShort());
    }

    @Test
    public void ReadOnlyBuffer(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);
        for (int i = 0; i < 64; i++) {
            byteBuffer.put((byte) i);
        }
        byteBuffer.flip();
        //得到一个只读的buffer
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        System.out.println(readOnlyBuffer.getClass());

        while (readOnlyBuffer.hasRemaining()){
            System.out.println(readOnlyBuffer.get());
        }
        //readOnlyBuffer.put((byte) 100);// ReadOnlyBufferException
    }


    @Test
    public void testMappedByteBuffer() throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile("F:\\testChannel.txt", "rw");
        FileChannel channel = randomAccessFile.getChannel();
        /**
         * FileChannel.MapMode.READ_WRITE:使用读写模式
         * 0:可以直接修改的起始位置
         * 5：是映射到内存的大小(不是索引位置),即文件的多少个字节映射到内存
         *
         * MappedByteBuffer实际类型是DirectByteBuffer
         */
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, 5);
        mappedByteBuffer.put(0, (byte) 'H');
        mappedByteBuffer.put(3, (byte) '9');
        channel.close();
    }

    /**
     * Scattering:将数据写入到buffer时,可以采用buffer数组,依次写入(分散)
     * Gathering:从buffer读取数据时,可以采用buffer数组,依次读
     */
    @Test
    public void testScatteringAndGathering() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);
        serverSocketChannel.socket().bind(inetSocketAddress);
        ByteBuffer[] buffers = new ByteBuffer[2];
        buffers[0] = ByteBuffer.allocate(5);
        buffers[1] = ByteBuffer.allocate(3);
        SocketChannel socketChannel = serverSocketChannel.accept();
        int messageLength = 8;
        while (true) {
            int byteRead = 0;
            while (byteRead < messageLength) {
                long l = socketChannel.read(buffers);
                byteRead += l;
                System.out.println("byteRead=" + byteRead);
                Arrays.asList(buffers).stream().map(buffer -> "position=" +
                        buffer.position() + ",limit=" + buffer.limit()).forEach(System.out::println);
            }
            Arrays.asList(buffers).forEach(buffer -> buffer.flip());
            long byteWirte = 0;
            while (byteWirte < messageLength) {
                long l = socketChannel.write(buffers);
                byteWirte += l;
            }
            Arrays.asList(buffers).forEach(buffer -> buffer.clear());
            System.out.println("byteRead=" + byteRead + ",byteWirte=" + byteWirte + ",messageLength=" + messageLength);
        }
    }

    /**
     * NIO非阻塞网络编程原理分析
     * 1.当客户端连接时,会通过ServerSocketChannel得到一个socketChannel
     * 2.将socketChannel注册到Selector上,通过父类的register(Selector sel, int ops),一个selector上可以注册多个socketChannel
     * 3.注册后返回一个SelectionKey,会和该Selector关联
     * 4.Selector进行监听select(),返回有事件发生的通道的个数
     * 5.进一步得到各个SelectionKey(有事件发生)
     * 6.再通过SelectionKey反向获取socketChannel,channel()
     * 7.可以通过得到的channel,完成业务处理
     */
    @Test
    public void TestNioServer() throws Exception {
        //创建ServerSocketChannel ==>>ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //得到一个Selector对象
        Selector selector = Selector.open();
        //绑定端口6666,在服务器监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置非阻塞
        serverSocketChannel.configureBlocking(false);
        //将serverSocketChannel注册到selector关心的事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //等待客户连接
        while (true) {
            if (selector.select(1000) == 0) {
                System.out.println("server wait 1s,not connect");
                continue;
            }
            //如果返回大于0,表示已经获取到关心的事件
            //selector.selectedKeys() 返回关注事件的集合,可以通过selectionKeys反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> KeyIterator = selectionKeys.iterator();
            while (KeyIterator.hasNext()) {
                //获取SelectionKey
                SelectionKey key = KeyIterator.next();
                //根据key对应的通道发生的事件做出处理
                if (key.isAcceptable()) {//OP_ACCEPT:有新的客户端连接
                    SocketChannel socketChannel = serverSocketChannel.accept();//该客户端生成一个SocketChannel
                    System.out.println("Client connection success" + socketChannel.hashCode());
                    //将SocketChannel设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //将当前socketChannel注册到Selector,关注事件为OP_READ,同时给socketChannel关联一个buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("客户端连接后,注册到SelectionKey的数量:" + selector.keys().size());
                }
                if (key.isReadable()) {//OP_READ:
                    //通过key反向获取对应的channel
                    SocketChannel channel = (SocketChannel) key.channel();
                    //获取到该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    channel.read(buffer);
                    System.out.println("from clients:" + new String(buffer.array()));
                }
                //手动从集合中移除SelectionKey,防止重复
                KeyIterator.remove();
            }
        }
    }
}
