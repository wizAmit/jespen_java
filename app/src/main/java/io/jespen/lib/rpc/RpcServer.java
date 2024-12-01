package io.jespen.lib.rpc;

import io.jespen.lib.Message;
import io.jespen.lib.ReqBuilder;
import io.jespen.lib.handlers.Broadcast;
import io.jespen.lib.handlers.NodeV2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcServer {

    private NodeV2 gossipNode;
    public RpcServer (Broadcast gossipNode) {
        this.gossipNode = gossipNode;
    }

    public void start() {
        int port = gossipNode.getRpcPorts.apply(gossipNode.getNodeId()).get(0);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.err.println("RPC Server started on port " + port);

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                while (true) {
                    var client = serverSocket.accept();
                    executor.submit(() -> {
                        System.err.println("Gossip Client Socket created on port " + client.getPort());

                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                            String inputLine = reader.readLine();
                            System.err.println("Gossip Client received: " + inputLine);
                            Message message = new ReqBuilder(inputLine).build();
                            System.err.println("RPC Message built: " + message);
                            this.gossipNode.handle(message);
                        } catch (IOException e) {
                            System.err.println("RPCSrvr IO Exception: " + e.getLocalizedMessage());
                        }
                    });
                }
            }

        } catch (Exception e) {
            System.err.println("Server Exception: " + e.getLocalizedMessage());
        }
    }
}
