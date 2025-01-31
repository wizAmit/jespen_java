package io.jespen.lib;

import com.eclipsesource.json.JsonObject;

public record UniqIdRes(int msg_id, int in_reply_to, String id) implements ResPayload {

    public UniqIdRes(UniqIdReqPd generateReqPd, int msg_id, String node_id) {
        this(msg_id, generateReqPd.getMsgId(),
                node_id +"-"+ msg_id);
    }

    @Override
    public int getInReplyTo() {
        return in_reply_to();
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.generate_ok;
    }

    @Override
    public int getMsgId() {
        return msg_id();
    }

    @Override
    public JsonObject getJsonObject() {
        return new JsonObject()
                .add("msg_id", msg_id())
                .add("in_reply_to", in_reply_to())
                .add("type", getMsgType().toString())
                .add("id", id());
    }
}
