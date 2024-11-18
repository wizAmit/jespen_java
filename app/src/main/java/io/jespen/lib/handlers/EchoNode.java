package io.jespen.lib.handlers;

import io.jespen.lib.InitReqPd;
import io.jespen.lib.Message;
import io.jespen.lib.ResBuilder;

public class EchoNode implements MessageHandler {
    private final Node node;

    public EchoNode(Node node) {
        this.node = node;
    }

    @Override
    public Message handle(Message req) {
//        System.out.println("ECHONode " + req);
        return ResBuilder.build(req, msgId.incrementAndGet());
    }

}
