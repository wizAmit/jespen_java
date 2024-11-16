package io.jespen.lib;

import com.fasterxml.jackson.databind.module.SimpleModule;

public interface ResPayload extends Payload {

    public int getInReplyTo();

}
