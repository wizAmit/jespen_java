package io.jespen.lib.handlers;

import io.jespen.lib.Message;
import io.jespen.lib.ResBuilder;

public class EchoNodeV2 {

    public static class EchoHandler implements MessageHandler {
        private final Node node;
        public EchoHandler(Node node) {
            this.node = node;
        }

        @Override
        public Message handle(Message req) {
            return ResBuilder.build(req, msgId.incrementAndGet());
        }
    }

}
