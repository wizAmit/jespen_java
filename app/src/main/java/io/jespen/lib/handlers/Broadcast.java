package io.jespen.lib.handlers;

import com.eclipsesource.json.JsonArray;
import io.jespen.lib.*;
import io.jespen.lib.rpc.SelectorCli;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Broadcast extends NodeV2 {

    protected final ConcurrentHashMap<Integer, Boolean> messages = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Boolean>> ackReceived = new ConcurrentHashMap<>();
    protected Gossip gossip;

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
            this.gossip = new Gossip(this.messages, this.ackReceived, this.topology.get());
            return handleInit(message);
        } else {
            return gossip.handleGossip(message, nodeId);
        }
    }

    @Override
    public Optional<ReqPayload> getRpcPayload(String neighbor) {
//        System.err.println("Generating RPC payload for " + neighbor);
        Map<Boolean, List<Integer>> divided = this.messages.keySet()
                .stream()
                .collect(Collectors.partitioningBy((i) -> this.getAckReceived().get(neighbor).containsKey(i)));

        int sz = Math.min((this.messages.size())/10, divided.get(false).size());
        var list = divided.get(false);
        for (int i = 0; i < sz; i++) {
            int idx = ThreadLocalRandom.current().nextInt(divided.get(true).size()) % sz;
//        if (divided.get(true).size() > 0) {
//            int idx = ThreadLocalRandom.current().nextInt(2);
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
}
