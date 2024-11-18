package io.jespen.lib.handlers;

import io.jespen.lib.Message;

import java.util.List;

public abstract class NodeV2 {

    String nodeId;
    List<String> neighbors;

    public abstract Message handle(Message message);

}
