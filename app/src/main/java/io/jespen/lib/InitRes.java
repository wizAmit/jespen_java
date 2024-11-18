package io.jespen.lib;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record InitRes(int msg_id, int in_reply_to) implements ResPayload {
    
    public InitRes(InitReqPd initReq, int msg_id) {
        this(msg_id, initReq.getMsgId());
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
        return MsgType.init_ok;
    }

    @Override
    public JsonObject getJsonObject() {
        return new JsonObject()
                .add("msg_id", msg_id())
                .add("in_reply_to", in_reply_to())
                .add("type", getMsgType().toString());
    }

}
