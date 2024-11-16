package io.jespen.lib;

import com.fasterxml.jackson.databind.JsonNode;

public record Headers(String src, String dest) {
    public Headers(JsonNode input) {
        this(input.get("src").asText(),
                input.get("dest").asText());
    }
}
