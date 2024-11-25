package io.jespen.lib.handlers;

import com.eclipsesource.json.JsonArray;
import io.jespen.lib.*;
import io.jespen.lib.rpc.RpcClient;
import io.jespen.lib.rpc.RpcServer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Broadcast extends NodeV2 {

    protected final ConcurrentHashMap<Integer, Boolean> messages = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Boolean>> ackReceived =
            new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, List<String>> topology = new ConcurrentHashMap<>();
    private final Gossip gossip = new Gossip();

    @Override
    public Message handle(Message message) {
        System.err.println("Handling " + message);
        List<Integer> srvrCliPort;
        if (neighbors != null && ackReceived.size() < neighbors.size()) {
            neighbors.forEach(n -> ackReceived.computeIfAbsent(n, k -> new ConcurrentHashMap<>()));

            srvrCliPort = getRpcPorts.apply(nodeId);

            CompletableFuture.runAsync(() -> {
                RpcServer rpcServer = new RpcServer();
                Gossip.schedule(() -> rpcServer.start(srvrCliPort.get(0)), gossip.scheduledGossipSrvr);
            });

        } else {
            srvrCliPort = new ArrayList<>();
        }

        if (message.msgType().equals(MsgType.broadcast)) {
            messages.put(((BroadcastReqPd) message.payload()).message(), true);

            return new Message(MsgType.broadcast_ok,
                    reverseHeaders.apply(message),
                    new BroadcastRes((BroadcastReqPd) message.payload(), msgId.incrementAndGet()));
        } else if (message.msgType().equals(MsgType.read)) {
            JsonArray bodyMsgs = new JsonArray();
            messages.keys().asIterator().forEachRemaining(bodyMsgs::add);

            for (String neighbors : this.topology.get(nodeId)) {
                System.err.println(neighbors);
                CompletableFuture.runAsync(() -> {
                    RpcClient rpcClient = new RpcClient();
                    rpcClient.start(getRpcPorts.apply(neighbors).get(0), message);
                }, gossip.scheduledGossipCli);
            }

            return new Message(MsgType.read_ok,
                    reverseHeaders.apply(message),
                    new ReadRes((ReadReqPd) message.payload(), msgId.incrementAndGet(), bodyMsgs));
        } else if (message.msgType().equals(MsgType.topology)) {
            this.topology.put(nodeId, ((TopologyReqPd) message.payload()).topology().get(nodeId));

            return new Message(MsgType.topology_ok,
                    reverseHeaders.apply(message),
                    new TopologyRes((TopologyReqPd) message.payload(), msgId.incrementAndGet()));
        } else if (message.msgType().equals(MsgType.init)) {
            return handleInit(message);
        } else {
            return gossip.handleGossip(message, nodeId);
        }
    }

    class Gossip {

        ExecutorService scheduledGossipSrvr = Executors.newVirtualThreadPerTaskExecutor();
        ExecutorService scheduledGossipCli = Executors.newVirtualThreadPerTaskExecutor();

        protected Message handleGossip(Message message, String nodeId) {
            System.err.println("Handling Gossip " + message);

            if (message.msgType().equals(MsgType.gossip)) {

                var known2them = ackReceived.get(message.headers().src());
                known2them.forEach((k, v) -> messages.putIfAbsent(k, false));

                return new Message(MsgType.gossip_ok,
                        reverseHeaders.apply(message),
                        new GossipRes((GossipReqPd) message.payload(), msgId.incrementAndGet()));
            } else if (message.msgType().equals(MsgType.gossip_ok)) {
                // Here there is actually the necessity to change return type to Optional!!!
                for (var nd : neighbors) {
                    Headers gossipTo = new Headers(nodeId, nd);
                    System.err.println("Gossiping to " + gossipTo);
                    CompletableFuture.runAsync(
                                    () -> schedule(() -> gossipReqSupplier.get(), scheduledGossipSrvr))
                            .thenApply(res -> new Message(MsgType.gossip, gossipTo, gossipReqSupplier.get()))
                            .thenAccept(System.out::println);
                }

                return null;
            } else {
                return handle(message);
            }
        }

        Supplier<GossipReqPd> gossipReqSupplier = () -> {
            System.err.println("Scheduling: ");
            var listSize = messages.size() / 3;
            var rndIndxs = ThreadLocalRandom.current().ints(listSize, 0, listSize);

            Set<Integer> msgIndxs = rndIndxs.boxed().collect(Collectors.toSet());

            List<Integer> listMsgs = new ArrayList<>();
            int i = 0;
            for (var entry : messages.entrySet()) {
                if (!msgIndxs.contains(i))
                    continue;
                listMsgs.add(entry.getKey());
                i++;
            }

            return new GossipReqPd(msgId.incrementAndGet(), listMsgs);
        };

        static void schedule(Runnable task, ExecutorService executorService) {
            System.err.println("Schedule running: ");
            executorService.submit(() -> {
                return executorService.submit(() -> {
                    try {
                        Thread.sleep(Duration.of(500, ChronoUnit.MILLIS));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    task.run();
                });
            });
        }

        Runnable gossipFire = () -> {
            System.err.println("Gossiping: ");
            neighbors.stream().filter(n -> !n.equals(nodeId)).forEach(n -> {
                Headers gossipTo = new Headers(nodeId, n);
                System.err.println("Gossiping to " + gossipTo);
                CompletableFuture.runAsync(
                                () -> schedule(() -> gossipReqSupplier.get(), scheduledGossipSrvr))
                        .thenApply(res -> new Message(MsgType.gossip, gossipTo, gossipReqSupplier.get()))
                        .thenAccept(System.out::println);
            });
        };

        BiConsumer<RpcClient, Message> gossipConsumer = (rpcClient, message) -> {
            System.err.println("Consuming gossip: ");
            rpcClient.start(getRpcPorts.apply(message.headers().src()).get(1), message);
        };

    }
}
