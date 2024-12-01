package io.jespen.lib.rpc;

import io.jespen.lib.*;
import io.jespen.lib.handlers.Broadcast;
import io.jespen.lib.handlers.NodeV2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class RpcClient {

    private NodeV2 gossipNode;
    private Supplier<Map<Integer, Socket>> socketSupplier;

    public RpcClient(Broadcast gossipNode) {
        System.err.println("RpcClient created");
        this.gossipNode = gossipNode;
    }

    public Map<Integer, Message> getNode2GossipMsg() {
        var neighbors = this.gossipNode.getTopology().get().get(this.gossipNode.getNodeId());
        Map<Integer, Message> gossipMessages = new HashMap<>();
        for (String neighbor : neighbors) {
            System.err.println(neighbor);
            gossipMessages.put(
                    gossipNode.getRpcPorts.apply(neighbor).get(0),
                    new Message(MsgType.gossip,
                            new Headers(this.gossipNode.getNodeId(), neighbor),
                            new GossipReqPd(NodeV2.msgId.incrementAndGet(),
                                    new ArrayList<>(((Broadcast) this.gossipNode).getMessages()
                                            .keySet()))));

        }
        return gossipMessages;
    }

    Function<Map.Entry<Integer, Message>, Optional<Message>> singleGossip = (Map.Entry<Integer, Message> entry) -> {
        try (Socket cliSocket = new Socket("localhost", entry.getKey())) {
            System.err.println(this.gossipNode.getNodeId() + " Client Socket created on port " + cliSocket.getPort());
            PrintWriter writer = new PrintWriter(cliSocket.getOutputStream(), true);
            writer.println(this.gossipNode.toJsonObject.apply(entry.getValue()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));
            return Optional.ofNullable(new ReqBuilder(reader.readLine()).build());
        } catch (Exception e) {
            System.err.println("Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    };

    public void task() {

        List<StructuredTaskScope.Subtask<Message>> futures = new ArrayList<>();
        try (StructuredTaskScope<Message> task = new StructuredTaskScope<>()) {

            for (Map.Entry<Integer, Message> entry : getNode2GossipMsg().entrySet()) {
                if (entry.getValue() == null) {
                    continue;
                }
                futures.add(task.fork(() -> singleGossip.apply(entry).orElse(null)));
            }

            task.join();
            futures.forEach(System.err::println);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    });


    //    public void start() throws IOException {
//        Map<Integer, Message> gossipMessages = getNode2GossipMsg();
//
//
//        int port = gossipNode.getRpcPorts.apply(gossipNode.getNodeId()).get(0);
//
//        try (Socket cliSocket = new Socket("localhost", port)) {
//            System.err.println(message.get().headers().src() + " Client Socket created on port " + cliSocket.getPort());
//
//            PrintWriter writer = new PrintWriter(cliSocket.getOutputStream(), true);
//            System.err.println("Client Socket created. Writing message: " + this.gossipNode.toJsonObject.apply(message.get()));
//            writer.println(this.gossipNode.toJsonObject.apply(message.get()));
//        } catch (Exception e) {
//            System.err.println("Exception: " + e.getLocalizedMessage());
//            e.printStackTrace();
//        }
//    }
    public void start() {
        System.err.println("Starting task within RpcClient: " + Thread.currentThread().getName());

        assert this.gossipNode.getTopology().get().get(this.gossipNode.getNodeId()) != null;
        System.err.println(this.gossipNode.getTopology().get().get(this.gossipNode.getNodeId()) == null);
        if (this.gossipNode.getTopology().get().get(this.gossipNode.getNodeId()) == null) {
            System.err.println("Do not schedule task");
            return;
        }

        scheduler.scheduleAtFixedRate(() -> {
            this.task();
        }, 500, 300, TimeUnit.MILLISECONDS);
    }
}