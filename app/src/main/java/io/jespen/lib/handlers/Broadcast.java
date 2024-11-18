package io.jespen.lib.handlers;

import com.eclipsesource.json.JsonArray;
import io.jespen.lib.*;

import java.util.concurrent.ConcurrentHashMap;

public class Broadcast extends NodeV2{

    private final ConcurrentHashMap<Integer, Boolean> messages = new ConcurrentHashMap<>();

    @Override
    public Message handle(Message message) {
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
            return new Message(MsgType.topology_ok,
                    reverseHeaders.apply(message),
                    new TopologyRes((TopologyReqPd) message.payload(), msgId.incrementAndGet()));
        }
        else {
            return handleInit(message);
        }
    }
}
