package io.jespen.bulider;

import io.jespen.lib.Message;
import io.jespen.lib.MsgType;
import io.jespen.lib.handlers.*;

import java.util.Map;


public class NodeBuilder {

    public static class TwoPartBuilder {
        private Message initMessage;
        private Node node;
        private NodeHandlers nodeHandler;

        final Map<MsgType, NodeHandlers> msgTypeMap = Map.ofEntries(
                Map.entry(MsgType.echo, NodeHandlers.Echo)
//                Map.entry(MsgType.init, MsgType.init_ok)
        );

        public Node getNode() {
            return node;
        }

        public TwoPartBuilder initTwoPartBuilder(Message message, NodeHandlers nodeHandler) {
            assert (message.msgType() == MsgType.init);
            this.nodeHandler = nodeHandler;

            this.initMessage = message;
            this.node = new Node();
            return this;
        }

        public Message handle(TwoPartBuilder builder) {
            builder.node.init(builder.initMessage);
            return builder.node.handle(builder.initMessage);
        }

        public MessageHandler getNodeHandler() {
            return new EchoNodeV2.EchoHandler(node);
        }
    }
}
