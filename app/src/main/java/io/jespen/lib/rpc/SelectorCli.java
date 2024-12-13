package io.jespen.lib.rpc;

import com.eclipsesource.json.JsonObject;
import io.jespen.lib.GossipReqPd;
import io.jespen.lib.Headers;
import io.jespen.lib.Message;
import io.jespen.lib.MsgType;
import io.jespen.lib.handlers.Broadcast;
import io.jespen.lib.handlers.NodeV2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SelectorCli implements AutoCloseable {
    private static ByteBuffer buffer;
    private static SelectorCli instance;
    private NodeV2 gossipNode;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public static SelectorCli start(Broadcast broadcast) {
        if (instance == null)
            instance = new SelectorCli(broadcast);

        scheduler.scheduleAtFixedRate(instance::sendGossip, 100, 300, TimeUnit.MILLISECONDS);

        return instance;
    }

    public static void stop() throws IOException {
        buffer = null;
    }

    private SelectorCli(Broadcast broadcast) {
        this.gossipNode = broadcast;
        buffer = ByteBuffer.allocate(1024);
    }

    private Message getGossipMsg(String neighbor) {
        return new Message(MsgType.gossip,
                new Headers(this.gossipNode.getNodeId(), neighbor),
                new GossipReqPd(NodeV2.msgId.incrementAndGet(),
                        new ArrayList<>(((Broadcast) this.gossipNode).getMessages()
                                .keySet())));
    }

    public void sendGossip() {

        for (String neighbor : this.gossipNode.getTopology().get().get(gossipNode.getNodeId())) {
            System.err.println("Sending gossip to neighbor: " + neighbor);
            try (SocketChannel socketChannel = SocketChannel.open()) {
                socketChannel.connect(new InetSocketAddress("127.0.0.1", gossipNode.getRpcPorts.apply(neighbor).get(0)));
                Message message = getGossipMsg(neighbor);
                JsonObject res = new JsonObject()
                        .add("src", message.headers().src())
                        .add("dest", message.headers().dest())
                        .add("body", message.payload().getJsonObject());

                this.buffer.clear().put(res.toString().getBytes()).flip();
                while (this.buffer.hasRemaining()) {
                    socketChannel.write(this.buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getLocalizedMessage());
            }
        }

    }

    @Override
    public void close() throws Exception {
        try {
            stop();
        } finally {
            scheduler.shutdown();
        }
    }
}
