package io.jespen.lib.handlers;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import io.jespen.lib.Message;
import io.jespen.lib.ResPayload;

public interface MessageHandler {

    AtomicInteger msgId = new AtomicInteger();

    public Message handle(Message req);

}
