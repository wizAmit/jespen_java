package io.jespen.lib;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ReqBuilder {
    private Headers headers;
    private ReqPayload payload;
    private MsgType msgType;

    public ReqBuilder(String reqString) throws IOException {
        JsonNode rootNode = ReqPayload.objectMapper.readTree(reqString);
        this.headers = new Headers(rootNode);
        this.msgType = MsgType.valueOf(rootNode.get("body").get("type").asText());

        this.payload = switch (this.msgType) {
            case echo -> new EchoReqPd(rootNode);
            case echo_ok -> null;
            case init -> new InitReqPd(rootNode);
            default -> null;
        };
    }

    public Message build() {
        return new Message(msgType, headers, payload);
    }
}
