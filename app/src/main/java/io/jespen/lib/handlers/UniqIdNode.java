package io.jespen.lib.handlers;

import io.jespen.lib.UniqIdReqPd;

import io.jespen.lib.Message;
import io.jespen.lib.MsgType;
import io.jespen.lib.UniqIdRes;

public class UniqIdNode extends NodeV2{

    @Override
    public Message handle(Message message) {
        if (message.msgType().equals(MsgType.generate)) {
            return new Message(MsgType.generate_ok,
                    reverseHeaders.apply(message),
                    new UniqIdRes((UniqIdReqPd) message.payload(), msgId.incrementAndGet(), nodeId));
        } else {
            return handleInit(message);
        }
    }
}
