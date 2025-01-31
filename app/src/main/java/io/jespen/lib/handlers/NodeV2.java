package io.jespen.lib.handlers;

import com.eclipsesource.json.JsonObject;
import io.jespen.lib.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class NodeV2 {

    protected NodeState state = NodeState.Uninitialized;
    protected Optional<ConcurrentHashMap<String, List<String>>> topology = Optional.empty();

    public static final AtomicInteger msgId = new AtomicInteger();

    Function<Message, Headers> reverseHeaders = (Message msg) -> {
        return new Headers(msg.headers().dest(), msg.headers().src());
    };

    public Function<String, List<Integer>> getRpcPorts = (String nodeId)  -> {
        int n = nodeId.charAt(1) - '0';
        return List.of(2*(2000+n)+1, 2*(2000+n)+2);
    };

    public Function<Message, JsonObject> toJsonObject = (Message message) -> {
        return new JsonObject()
                .add("src", message.headers().src())
                .add("dest", message.headers().dest())

                .add("body", message.payload().getJsonObject());
    };

    protected String nodeId;
    List<String> neighbors;

    public abstract Message handle(Message message);

    protected Message handleInit(Message message) {
        this.nodeId = ((InitReqPd) message.payload()).node_id();
        this.neighbors = ((InitReqPd) message.payload()).node_ids();
        this.state.nextState();
        return new Message(MsgType.init_ok,
                reverseHeaders.apply(message),
                new InitRes((InitReqPd) message.payload(), msgId.incrementAndGet()));
    }

    public String getNodeId() {
        return nodeId;
    }

    public List<String> getNeighbors() {
        return neighbors;
    }

    public Optional<ConcurrentHashMap<String, List<String>>> getTopology() {
        return topology;
    }

    public Optional<ReqPayload> getRpcPayload(String neighbor) { return Optional.empty(); }

    public void updateTopology (String neighbor, Message message) {}

    public Message handleGossip(Message message, String neighbor) { return null; }
}
