package io.jespen.lib;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.List;

public record GossipReqPd(int msg_id, List<Integer> known2other) implements ReqPayload {

    public GossipReqPd(JsonNode input) {
        this(input.get("body").get("msg_id").asInt(),
                Arrays.asList(objectMapper.convertValue(input.get("body").get("messages"), Integer[].class)));
//                input.get("body").get("known2other")
//                        .elements()
//                        .stream()
//                        .map(jsonVal -> jsonVal.asInt())
//                        .collect(Collectors.toList()));
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.gossip;
    }

    @Override
    public int getMsgId() {
        return msg_id();
    }

    @Override
    public JsonObject getJsonObject() {
        JsonArray jsonArrMsgs = new JsonArray();
        for (Integer integer : known2other) {
            jsonArrMsgs.add(integer);
        }

        return new JsonObject()
                .add("msg_id", msg_id())
                .add("type", getMsgType().toString())
                .add("messages", jsonArrMsgs);
    }
}
