package com.syf.nio;

import com.syf.nio.api.Request;
import com.syf.nio.api.Response;
import com.syf.nio.client.Connection;
import com.syf.nio.client.NioClient;
import com.syf.nio.server.NioServer;
import org.junit.Test;

import java.io.IOException;

public class NioServerTest {
    @Test
    public void TestServerStart() {
        try {
            new NioServer(8080).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestClient() throws IOException {
        Connection connect = NioClient.newConnect("localhost", 8080);
        try {
            for (int i = 0; i < 5; i++) {
                Response response = connect.sendMsg(new Request("hello"));
                if (response != null) {
                    System.out.println(response);
                }
            }
            connect.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
