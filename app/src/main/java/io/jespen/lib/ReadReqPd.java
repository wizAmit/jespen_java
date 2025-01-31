package io.jespen.lib;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;

public record ReadReqPd(int msg_id) implements ReqPayload {

    public ReadReqPd(JsonNode input) {
        this(input.get("body").get("msg_id").asInt());
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.read;
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
