package io.jespen.lib.handlers;

import com.eclipsesource.json.JsonArray;
import io.jespen.lib.*;
import io.jespen.lib.rpc.SelectorCli;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Broadcast extends NodeV2 {

    protected final ConcurrentHashMap<Integer, Boolean> messages = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Boolean>> ackReceived = new ConcurrentHashMap<>();

    public Broadcast() {
        super();
        this.topology = Optional.of(new ConcurrentHashMap<>());
    }

    public ConcurrentHashMap<Integer, Boolean> getMessages() {
        return messages;
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<Integer, Boolean>> getAckReceived() {
        return ackReceived;
    }

    @Override
    public Message handle(Message message) {
//        System.err.println("Handling " + message);
        if (neighbors != null && ackReceived.size() < neighbors.size()) {
            neighbors.forEach(n -> ackReceived.computeIfAbsent(n, k -> new ConcurrentHashMap<>()));
        }

        if (message.msgType().equals(MsgType.broadcast)) {
            messages.put(((BroadcastReqPd) message.payload()).message(), true);

            return new Message(MsgType.broadcast_ok,
                    reverseHeaders.apply(message),
                    new BroadcastRes((BroadcastReqPd) message.payload(), msgId.incrementAndGet()));
        } else if (message.msgType().equals(MsgType.read)) {
            JsonArray bodyMsgs = new JsonArray();
            messages.keys().asIterator().forEachRemaining(bodyMsgs::add);

            return new Message(MsgType.read_ok,
                    reverseHeaders.apply(message),
                    new ReadRes((ReadReqPd) message.payload(), msgId.incrementAndGet(), bodyMsgs));
        } else if (message.msgType().equals(MsgType.topology)) {
            this.topology.get().put(nodeId, ((TopologyReqPd) message.payload()).topology().get(nodeId));

            SelectorCli selectorCli = SelectorCli.start(this);
            return new Message(MsgType.topology_ok,
                    reverseHeaders.apply(message),
                    new TopologyRes((TopologyReqPd) message.payload(), msgId.incrementAndGet()));
        } else if (message.msgType().equals(MsgType.init)) {
            return handleInit(message);
        } else {
            return this.handleGossip(message, nodeId);
        }
    }

    @Override
    public Optional<ReqPayload> getRpcPayload(String neighbor) {
        Map<Boolean, List<Integer>> divided = this.messages.keySet()
                .stream()
                .collect(Collectors.partitioningBy((i) -> this.getAckReceived().get(neighbor).containsKey(i)));

        int sz = Math.min((this.messages.size())/10, divided.get(false).size());
        var list = divided.get(false);
        for (int i = 0; i < sz; i++) {
            int idx = ThreadLocalRandom.current().nextInt(divided.get(true).size()) % sz;
            list.add(divided.get(true).get(idx));
        }

        GossipReqPd gossipReqPd = new GossipReqPd(msgId.incrementAndGet(), list);
        System.err.println(neighbor + " => RPC Payload size: " + list.size());
        return Optional.of(gossipReqPd);
    }

    @Override
    public void updateTopology(String neighbor, Message message) {
        ((GossipReqPd) message.payload()).known2other().forEach(i -> this.ackReceived.get(neighbor).put(i, true));
    }

    @Override
    public Message handleGossip(Message message, String nodeId) {
//        System.err.println("Handling Gossip " + message);

        if (message.msgType().equals(MsgType.gossip)) {

            for (Integer i : ((GossipReqPd) message.payload()).known2other()) {
                this.ackReceived.get(message.headers().src()).put(i, true);
                this.messages.putIfAbsent(i, true);

                System.err.println("messages: " + messages);
                System.err.println("ackReceived: " + ackReceived);
            }


            return new Message(MsgType.gossip_ok,
                    reverseHeaders.apply(message),
                    new GossipRes((GossipReqPd) message.payload(), msgId.incrementAndGet()));
        } else if (message.msgType().equals(MsgType.gossip_ok)) {
            // Here there is actually the necessity to change return type to Optional!!!
            System.err.println("Not handling gossip_ok");
            return null;
        } else {
            throw new RuntimeException("Should Not happen: " + message.msgType());
        }
    }
}
