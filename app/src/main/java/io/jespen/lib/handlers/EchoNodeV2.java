package io.jespen.lib.handlers;

import io.jespen.lib.*;

import static io.jespen.lib.handlers.NodeV2.msgId;

public class EchoNodeV2 extends NodeV2 {

    @Override
    public Message handle(Message message) {
//        System.err.println("Handling " + message);
        if (message.msgType().equals(MsgType.echo)) {
            return new Message(MsgType.echo_ok,
                    reverseHeaders.apply(message),
                    new EchoRes((EchoReqPd) message.payload(), msgId.incrementAndGet()));
        } else {
            return handleInit(message);
        }
    }
}
