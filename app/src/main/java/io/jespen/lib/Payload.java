package io.jespen.lib;

import com.eclipsesource.json.JsonObject;

import java.util.EnumMap;

public interface Payload {

    
    public EnumMap<MsgType,MsgType> echoTypes = new EnumMap<MsgType,MsgType>(MsgType.class){
                                                    {
                                                        put(MsgType.echo, MsgType.echo_ok);
                                                        put(MsgType.init, MsgType.init_ok);
                                                    }
                                                };

    public MsgType getMsgType();

    public int getMsgId();

    public JsonObject getJsonObject();
    
}
