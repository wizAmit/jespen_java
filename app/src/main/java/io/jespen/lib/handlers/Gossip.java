package io.jespen.lib.handlers;

import io.jespen.lib.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

class Gossip extends NodeV2 {

    ExecutorService scheduledGossipSrvr = Executors.newVirtualThreadPerTaskExecutor();
    ExecutorService scheduledGossipCli = Executors.newVirtualThreadPerTaskExecutor();

    ConcurrentHashMap<Integer, Boolean> messages;
    ConcurrentHashMap<String, ConcurrentHashMap<Integer, Boolean>> ackReceived;
    ConcurrentHashMap<String, List<String>> topology;

    public Gossip(ConcurrentHashMap<Integer, Boolean> messages,
                  ConcurrentHashMap<String, ConcurrentHashMap<Integer, Boolean>> ackReceived,
                  ConcurrentHashMap<String, List<String>> topology) {
        this.messages = messages;
        this.ackReceived = ackReceived;
        this.topology = topology;
    }

    protected Message handleGossip(Message message, String nodeId) {
        System.err.println("Handling Gossip " + message);

        if (message.msgType().equals(MsgType.gossip)) {

//                var known2them = ackReceived.get(message.headers().src());
//                known2them.forEach((k, v) -> messages.putIfAbsent(k, false));

            System.err.println("B4 size: " + messages.size());
            for (Integer i : ((GossipReqPd) message.payload()).known2other()) {
                ackReceived.computeIfAbsent(message.headers().src(), k -> new ConcurrentHashMap<>());
                ackReceived.get(message.headers().src()).put(i, true);
                messages.putIfAbsent(i, true);
            }
            System.err.println("AFTER size: " + messages.size());


            return new Message(MsgType.gossip_ok,
                    reverseHeaders.apply(message),
                    new GossipRes((GossipReqPd) message.payload(), msgId.incrementAndGet()));
        } else if (message.msgType().equals(MsgType.gossip_ok)) {
            // Here there is actually the necessity to change return type to Optional!!!
            for (var nd : neighbors) {
                Headers gossipTo = new Headers(nodeId, nd);
                System.err.println("Gossiping to " + gossipTo);

            }

            return null;
        } else {
            throw new RuntimeException("Should Not happen: " + message.msgType());
        }
    }

    @Override
    public Message handle(Message message) {
        return handleGossip(message, topology.keySet().iterator().next());
    }

}
