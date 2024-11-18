package io.jespen.lib;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;

public record ReadRes(int msg_id, int in_reply_to, JsonArray messages) implements ResPayload {

    public ReadRes(ReadReqPd readReqPd, int msg_id, JsonArray messages) {
        this(msg_id, readReqPd.getMsgId(), messages);
    }

    @Override
    public int getInReplyTo() {
        return in_reply_to();
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.read_ok;
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
                .add("messages", messages());
    }
}
