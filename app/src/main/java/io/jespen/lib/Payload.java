package io.jespen.lib;

import com.eclipsesource.json.JsonObject;

import java.util.EnumMap;

public interface Payload {

    public MsgType getMsgType();

    public int getMsgId();

    public JsonObject getJsonObject();
    
}
