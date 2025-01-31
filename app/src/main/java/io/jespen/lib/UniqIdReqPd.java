package io.jespen.lib;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;

public record UniqIdReqPd(int msg_id) implements ReqPayload {

    public UniqIdReqPd(JsonNode input) {
        this(input.get("body").get("msg_id").asInt());
    }

    @Override
    public int getMsgId() {
        return msg_id;
    }

    @Override
    public JsonObject getJsonObject() {
        return null;
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.echo;
    }
}
