package io.jespen.lib;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ReqPayload extends Payload {

    final ObjectMapper objectMapper = new ObjectMapper();

}
