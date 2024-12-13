package io.jespen.lib.handlers;

import com.eclipsesource.json.JsonArray;
import io.jespen.lib.*;
import io.jespen.lib.rpc.SelectorCli;

import java.beans.PropertyChangeSupport;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

public class Broadcast extends NodeV2 {

    protected final ConcurrentHashMap<Integer, Boolean> messages = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, ConcurrentHashMap<Integer, Boolean>> ackReceived = new ConcurrentHashMap<>();
    protected Gossip gossip;

    private final PropertyChangeSupport topologyChangeSupport = new PropertyChangeSupport(this);

    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    });

    public Broadcast() {
        super();
        this.topology = Optional.of(new ConcurrentHashMap<>());
    }

    public ConcurrentHashMap<Integer, Boolean> getMessages() {
        return messages;
    }

    @Override
    public Message handle(Message message) {
        System.err.println("Handling " + message);
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

}
