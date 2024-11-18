package io.jespen.lib;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;

public record TopologyReqPd(int msg_id, JsonNode topology) implements ReqPayload {

    public TopologyReqPd(JsonNode obj) {
        this(obj.get("body").get("msg_id").asInt(), obj.get("topology"));
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.topology;
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
