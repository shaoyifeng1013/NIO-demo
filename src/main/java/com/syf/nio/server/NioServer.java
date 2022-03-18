package com.syf.nio.server;

import com.syf.nio.echo.EchoHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
    private Selector selector;
    private int port;
    private long selectTimeout;

    public NioServer(int port) {
        this.port = port;
        this.selectTimeout = 5000L;
    }

    public void start() throws IOException {
        selector = Selector.open();
        //创建 ServerSocketChannel 实例，配置为非阻塞模式，绑定本地端口。
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        EchoHandler echoHandler = new EchoHandler();

        //开始阻塞，等待多路复用器返回
        while (true) {
            //阻塞
            selector.select();
            //获得事件
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            /**
             * 处理 SelectionKey 的感兴趣的操作。注册到 selector 中的 serverSocketChannel 只能是
             *  isAcceptable() ，因此通过它的 accept() 方法，我们可以获取到客户端的请求 SocketChannel 实例，
             *  然后再把这个 socketChannel 注册到 selector中，设置为可读的操作。那么下次遍历
             *  selectionKeys 的时候，就可以处理那么可读的操作
             */
            while (iter.hasNext()) {
                SelectionKey key = iter.next();

                //如果可Accept，将可Read
                if (key.isAcceptable()) {
                    register(selector, serverSocket);
                }

                //read
                if (key.isReadable()) {
                    try {
                        echoHandler.read(key);
                    } catch (Exception e) {
                        System.out.printf("server: read error , reason : %s\n", e.getMessage());
                        key.cancel();
                        continue;
                    }
                }

                //write
                if (key.isValid() && key.isWritable()) {
                    try {
                        echoHandler.write(key);
                    } catch (IOException e) {
                        System.out.printf("server: write error , reason : %s\n", e.getMessage());
                        key.cancel();
                    }
                }
                iter.remove();
            }

        }
    }

    private void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }
}
