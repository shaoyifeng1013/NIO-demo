package com.syf.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class NioClient {
    public static Connection newConnect(String ip, int port) throws IOException {
        SocketChannel channel = SocketChannel.open(new InetSocketAddress(ip, port));
        return new Connection(channel);
    }
}
