package io.jespen.lib.handlers;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import io.jespen.lib.EchoRes;
import io.jespen.lib.Message;
import io.jespen.lib.ResBuilder;

public class EchoNode implements MessageHandler {

    @Override
    public Message handle(Message req) {
        return new ResBuilder(req, msgId.incrementAndGet()).build(); 
        
    }
}
