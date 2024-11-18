package io.jespen.lib;

import io.jespen.lib.handlers.NodeHandlers;

import java.util.Map;

public final class ResBuilder {

    static final Map<MsgType, MsgType> msgTypeMap = Map.ofEntries(
            Map.entry(MsgType.echo, MsgType.echo_ok),
            Map.entry(MsgType.init, MsgType.init_ok)
    );

    public ResBuilder(){}

    public static Message build(Message message, int msg_id) {
        Headers headers = new Headers(message.headers().dest(), message.headers().src());
//        System.out.println(message.msgType());
        MsgType msgType = msgTypeMap.get(message.msgType());
//        System.out.println(msgType);
//        assert(this != null);

        ResPayload payload;
        switch (msgType) {
            case init, init_ok -> {
                payload = new InitRes((InitReqPd)message.payload(), msg_id);
                break;
            }
            case echo_ok -> {
                payload = new EchoRes((EchoReqPd)message.payload(), msg_id);
                break;
            }
            default -> {
                payload = null;
                break;
            }
        }

        return new Message(msgType, headers, payload);
    }
}
