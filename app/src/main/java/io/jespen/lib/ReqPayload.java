package io.jespen.lib;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ReqPayload extends Payload {

    ObjectMapper objectMapper = new ObjectMapper();

}
