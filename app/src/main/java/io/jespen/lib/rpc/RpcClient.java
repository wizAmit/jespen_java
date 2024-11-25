package io.jespen.lib.rpc;

import io.jespen.lib.Message;
import io.jespen.lib.handlers.Broadcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcClient extends Broadcast {

    public void start(final int port, final Message message) {

        try (Socket cliSocket = new Socket("localhost", port)) {
            System.err.println(nodeId + " Client Socket created on port " + port);
//            try (ExecutorService rpcCliExecutor = Executors.newVirtualThreadPerTaskExecutor();) {
//                rpcCliExecutor.submit(() -> {
                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(cliSocket.getOutputStream(), true);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));
                        System.err.println("Client Socket created. Writing message: " + toJsonObject.apply(message));
                        writer.println(toJsonObject.apply(message));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
//                });
//            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }
}