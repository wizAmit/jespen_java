package io.jespen.lib.handlers;

import io.jespen.lib.Message;
import io.jespen.lib.ResBuilder;

public class InitNode implements MessageHandler {

    @Override
    public Message handle(Message req) {
        return new ResBuilder(req, msgId.incrementAndGet()).build();
    }
}
