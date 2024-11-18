package io.jespen.lib;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;

public record BroadcastReqPd(int msg_id, int message) implements ReqPayload {

    public BroadcastReqPd(JsonNode input) {
        this(input.get("body").get("msg_id").asInt(),
                input.get("body").get("message").asInt());
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.broadcast;
    }

    @Override
    public int getMsgId() {
        return msg_id();
    }

    @Override
    public JsonObject getJsonObject() {
        return null;
    }
}
