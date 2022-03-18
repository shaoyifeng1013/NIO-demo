package com.syf.nio.client;

import com.google.gson.Gson;
import com.syf.nio.api.Request;
import com.syf.nio.api.Response;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Connection {

    private SocketChannel channel;
    private ByteBuffer byteBuffer;

    public Connection(SocketChannel channel) {
        this.channel = channel;
        this.byteBuffer = ByteBuffer.allocate(256);
    }

    public void stop() throws IOException {
        this.channel.close();
        byteBuffer = null;
    }

    public Response sendMsg(Request request) throws IOException {
        if (!channel.isConnected()) {
            return new Response(403, "not connected server");
        }
        Gson gson = new Gson();
        String reqJson = gson.toJson(request);
        byteBuffer.put(reqJson.getBytes(StandardCharsets.UTF_8));
        this.byteBuffer.flip();
        channel.write(this.byteBuffer);
        this.byteBuffer.clear();

        channel.read(this.byteBuffer);
        String responseStr = new String(byteBuffer.array()).trim();
        this.byteBuffer.clear();

        Response response = gson.fromJson(responseStr, Response.class);
        return response;
    }
}
