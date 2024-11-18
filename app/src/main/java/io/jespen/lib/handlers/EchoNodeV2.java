package io.jespen.lib.handlers;

import io.jespen.lib.*;

public class EchoNodeV2 extends NodeV2 implements MessageHandler {

    @Override
    public Message handle(Message message) {
//        System.err.println("Handling " + message);
        return
                message.msgType().equals(MsgType.echo) ?
                new Message(MsgType.echo_ok,
                reverseHeaders.apply( message ),
                new EchoRes((EchoReqPd)message.payload(), msgId.incrementAndGet()))
                        :
                new Message(MsgType.init_ok,
                        reverseHeaders.apply( message ),
                        new InitRes((InitReqPd)message.payload(), msgId.incrementAndGet()));
    }
}
