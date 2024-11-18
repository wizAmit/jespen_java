package io.jespen.lib;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.annotation.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EchoRes(int msg_id, int in_reply_to, String echo) implements ResPayload {

    public EchoRes(EchoReqPd echoReq, int msg_id) {
        this(msg_id, echoReq.getMsgId(), echoReq.echo());
    }

    @JsonIgnore
    @Override
    public int getInReplyTo() {
        return in_reply_to();
    }

    @JsonIgnore
    @Override
    public int getMsgId() {
        return msg_id();
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.echo_ok;
    }

    @Override
    public JsonObject getJsonObject() {
        return new JsonObject()
                .add("msg_id", msg_id())
                .add("in_reply_to", in_reply_to())
                .add("type", getMsgType().toString())
                .add("echo", echo());
    }

}
