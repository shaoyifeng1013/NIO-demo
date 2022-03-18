package com.syf.nio.echo;

import com.google.gson.Gson;
import com.syf.nio.api.Request;
import com.syf.nio.api.Response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class EchoHandler {

    private int bufferSize = 256;

    public void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(bufferSize);
        int readFlag = channel.read(readBuffer);
        //可以识别到客户端关闭
        if (readFlag == 0
                || readFlag == -1) {
            System.out.println("server: closed.......");
            channel.close();
            return;
        }
        String req = new String(readBuffer.array()).trim();

        if (req.trim().toLowerCase(Locale.ROOT).equals("close")) {
            System.out.println("server: closed.......");
            channel.close();
            return;
        }

        System.out.println("server: accept req success");
        Gson gson = new Gson();
        Request reqObj = gson.fromJson(req, Request.class);
        key.attach(reqObj);
        //感兴趣 write
        key.interestOps(SelectionKey.OP_WRITE);
    }

    public void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        Object attachment = key.attachment();
        if (attachment == null) {
            System.out.println("server: attachment is null");
            return;
        }
        Request req = (Request) attachment;
        System.out.println("server: accept req msg = " + req.msg);
        Response response = new Response(200, "success");
        Gson gson = new Gson();
        ByteBuffer reqStr = ByteBuffer.wrap(gson.toJson(response).getBytes(StandardCharsets.UTF_8));
        //read结束要翻转，用于反向遍历给写操作
//        reqStr.flip();
        channel.write(reqStr);
        key.interestOps(SelectionKey.OP_READ);
    }
}
