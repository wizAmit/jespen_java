package io.jespen.lib;

import com.eclipsesource.json.JsonObject;

public record GossipRes(int msg_id, int in_reply_to) implements ResPayload {

    public GossipRes(GossipReqPd gossipReqPd, int msg_id) {
        this(msg_id, gossipReqPd.getMsgId());
    }

    @Override
    public int getInReplyTo() {
        return in_reply_to();
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.gossip_ok;
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
                .add("type", getMsgType().toString());
    }
}
