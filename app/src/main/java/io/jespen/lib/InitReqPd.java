package io.jespen.lib;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public record InitReqPd(int msg_id, String node_id, List<String> node_ids) implements ReqPayload {

    public InitReqPd(JsonNode input) {
        this(input.get("body").get("msg_id").asInt(),
                input.get("body").get("node_id").asText(),
                Arrays.asList(objectMapper.convertValue(input.get("body").get("node_ids"), String[].class))
                );
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
        return MsgType.init;
    }

}
