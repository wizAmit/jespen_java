package io.jespen.lib;

import com.fasterxml.jackson.annotation.*;

public record InitRes(int msg_id, int reply_to) implements ResPayload {
    
    public InitRes(InitReqPd initReq, int msg_id) {
        this(msg_id, initReq.getMsgId());
    }

    @JsonIgnore
    @Override
    public int getInReplyTo() {
        return reply_to();
    }

    @JsonIgnore
    @Override
    public int getMsgId() {
        return msg_id();
    }

    @JsonProperty("type")
    @Override
    public MsgType getMsgType() {
        return MsgType.init_ok;
    }
}
