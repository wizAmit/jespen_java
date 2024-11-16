package io.jespen.lib;

public class ResBuilder {
    private Headers headers;
    private ResPayload payload;
    private MsgType msgType;

    public ResBuilder(Message message, int msg_id) {
        this.headers = new Headers(message.headers().dest(), message.headers().src());
        this.msgType = Payload.echoTypes.get(message.msgType());

        this.payload = switch (this.msgType) {
            case echo -> null;
            case echo_ok -> new EchoRes((EchoReqPd)message.payload(), msg_id);
            case init -> null;
            case init_ok -> new InitRes((InitReqPd)message.payload(), msg_id);
            default -> null;
        };
    }

    public Message build() {
        return new Message(msgType, headers, payload);
    }
}
