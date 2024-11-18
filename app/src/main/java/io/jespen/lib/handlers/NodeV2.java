package io.jespen.lib.handlers;

import io.jespen.lib.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public abstract class NodeV2 {

    protected NodeState state = NodeState.Uninitialized;

    static final AtomicInteger msgId = new AtomicInteger();

    Function<Message, Headers> reverseHeaders = (Message msg) -> {
        return new Headers(msg.headers().dest(), msg.headers().src());
    };

    String nodeId;
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

}
