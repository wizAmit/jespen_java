package io.jespen.lib;

import com.eclipsesource.json.JsonObject;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.Serializable;

public interface ResPayload extends Payload {

    public int getInReplyTo();

}
