package io.jespen.lib;

import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.*;

public record Message(@JsonIgnore MsgType msgType, Headers headers, Payload payload) implements Serializable {

    private static class MessageProxy implements Serializable {

        private static final long serialVersionUID = 8333905273185431754L;

        private String src;
        private String dest;
        private Payload body;

        public MessageProxy(Message m) {
            this.src = m.headers().src();
            this.dest = m.headers().dest();
            this.body = m.payload();
        }

    }

    private Object writeReplace() {
        return new MessageProxy(this);
    }
}
