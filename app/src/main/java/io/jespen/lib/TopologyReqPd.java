package io.jespen.lib;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

public record TopologyReqPd(int msg_id, Map<String, List<String>> topology) implements ReqPayload {

    public TopologyReqPd(JsonNode obj) {
        this(obj.get("body").get("msg_id").asInt(), objectMapper.convertValue(obj.get("body").get("topology"), Map.class));
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
