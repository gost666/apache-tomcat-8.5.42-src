package testNetty.server;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer {
    /**
     * 测试方式:
     * 通过cmd命令 ==>> telnet 127.0.0.1 6666 ==>> Ctrl+] ==>> send hello100
     *
     * @throws Exception
     */

    @Test
    public void testBIO() throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(6666);
        System.out.println("server begin..");
        while (true) {
            System.out.println("ThreadInfo ThreadName=" + Thread.currentThread().getName() +
                    ",ThreadId=" + Thread.currentThread().getId());
            System.out.println("wait connect");
            final Socket socket = serverSocket.accept();
            System.out.println("connection first server");
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    //可以和客户端通讯
                    handler(socket);
                }
            });
        }
    }

    public static void handler(Socket socket) {
        try {
            System.out.println("ThreadInfo ThreadName=" + Thread.currentThread().getName() +
                    ",ThreadId=" + Thread.currentThread().getId());
            byte[] bytes = new byte[1024];
            //通过socket获取输入流
            InputStream inputStream = socket.getInputStream();
            while (true) {
                System.out.println("ThreadInfo ThreadName=" + Thread.currentThread().getName() +
                        ",ThreadId=" + Thread.currentThread().getId());
                System.out.println("read....");
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.println(new java.lang.String(bytes, 0, read));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("close client connection");
        }
    }
}

