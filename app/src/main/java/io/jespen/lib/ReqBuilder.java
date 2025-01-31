package io.jespen.lib;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class ReqBuilder {
    private Headers headers;
    private ReqPayload payload;
    private MsgType msgType;

    public ReqBuilder(String reqString) {
        System.err.println("ReqBuilder Reading " + reqString);
        try {
            JsonNode rootNode = new ObjectMapper().readTree(reqString);
            this.headers = new Headers(rootNode);
            this.msgType = MsgType.valueOf(rootNode.get("body").get("type").asText());

            // ToDO: Growth starts here
            this.payload = switch (this.msgType) {
                case echo -> new EchoReqPd(rootNode);
                case echo_ok -> null;
                case init -> new InitReqPd(rootNode);
                case generate -> new UniqIdReqPd(rootNode);
                case generate_ok -> null;
                case broadcast -> new BroadcastReqPd(rootNode);
                case broadcast_ok -> null;
                case read -> new ReadReqPd(rootNode);
                case read_ok -> null;
                case topology -> new TopologyReqPd(rootNode);
                case topology_ok -> null;
                case gossip -> new GossipReqPd(rootNode);
                case gossip_ok -> null;
                default -> null;
            };
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSON parse error");
        }

    }

    public Message build() {
        return new Message(msgType, headers, payload);
    }
}
