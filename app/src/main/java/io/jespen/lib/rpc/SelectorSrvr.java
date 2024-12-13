package io.jespen.lib.rpc;

import io.jespen.lib.ReqBuilder;
import io.jespen.lib.handlers.Broadcast;
import io.jespen.lib.handlers.NodeV2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public class SelectorSrvr {

    private NodeV2 gossipNode;

    public SelectorSrvr(Broadcast gossipNode) throws IOException {
        this.gossipNode = gossipNode;
    }

    private void answerWithEcho(ByteBuffer buffer, SelectionKey key)
            throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        int r = client.read(buffer);
        if (r == -1) {
            System.err.println("Client disconnected: " + client.socket().getInetAddress() + ":" + client.socket().getPort());
            client.close();
        }
        else {
            buffer.flip();
            String msg = new String(buffer.array()).trim();
            System.err.println(this.gossipNode.getNodeId() + " Server received: " + msg);
            CompletableFuture.runAsync(() -> gossipNode.handle(new ReqBuilder(msg).build()));
            buffer.clear();
        }
    }

    private static SocketChannel register(Selector selector, ServerSocketChannel serverSocket)
            throws IOException {

        SocketChannel client = serverSocket.accept();
        Socket cliSocket = client.socket();
        System.err.println("RpcClient connected: " + cliSocket.getInetAddress() + ":" + cliSocket.getPort());
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        return client;
    }

    public void start() throws IOException {
        HashSet<SocketChannel> clients = new HashSet<SocketChannel>();
        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            System.err.println("RPC Server created");
            serverSocket.configureBlocking(false);
            InetSocketAddress port = new InetSocketAddress("0.0.0.0", gossipNode.getRpcPorts.apply(gossipNode.getNodeId()).get(0));
            serverSocket.bind(port);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (true) {
                if (selector.select() == 0) {
                    continue;
                }
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isAcceptable()) {
                        SocketChannel client = register(selector, serverSocket);
                        clients.add(client);
                    } else if (key.isReadable()) {
                        if (key.channel() instanceof SocketChannel client) {
                            answerWithEcho(buffer, key);
                        } else {
                            System.err.println("Invalid Client disconnected.");
                        }
                    }
                }
                selector.selectedKeys().clear();
            }
        } catch (Exception e) {
            System.err.println("RPC Server Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            for (SocketChannel client : clients) {
                try {
                    client.close();
                } catch (IOException e) {
                    System.err.println("Error Closing clients: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
    }

}
