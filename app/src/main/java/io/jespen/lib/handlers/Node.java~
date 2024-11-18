package io.jespen.lib.handlers;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.jespen.lib.InitReqPd;
import io.jespen.lib.Message;
import io.jespen.lib.ReqPayload;
import io.jespen.lib.ResBuilder;

public class Node implements MessageHandler {

    String nodeId;
    List<String> neighbors;

    public void init (Message message) {
        this.nodeId = ((InitReqPd) message.payload()).node_id();
        this.neighbors = ((InitReqPd) message.payload()).node_ids();
    };

    @Override
    public Message handle(Message req) {
        return ResBuilder.build(req, msgId.incrementAndGet());
    }

    public String getNodeId() {
        return nodeId;
    }

    public List<String> getNeighbors() {
        return neighbors;
    }
}
