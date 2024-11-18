package io.jespen.lib;

import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.*;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

public record Message(@JsonIgnore MsgType msgType, Headers headers, Payload payload) {

}
