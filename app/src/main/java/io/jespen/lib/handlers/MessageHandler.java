package io.jespen.lib.handlers;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import io.jespen.lib.Headers;
import io.jespen.lib.InitReqPd;
import io.jespen.lib.Message;
import io.jespen.lib.ResBuilder;

public interface MessageHandler {

    static final AtomicInteger msgId = new AtomicInteger();

    Function<Message, Headers> reverseHeaders = (Message msg) -> {
        return new Headers(msg.headers().dest(), msg.headers().src());
    };

//    public Function<Message, Message> init = Function.identity();

//    public Message handle(Message req);

}
