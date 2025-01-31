package io.jespen.lib;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;

public record EchoReqPd(int msg_id, String echo) implements ReqPayload {
    public EchoReqPd(JsonNode input) {
        this(input.get("body").get("msg_id").asInt(),
                input.get("body").get("echo").asText());
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