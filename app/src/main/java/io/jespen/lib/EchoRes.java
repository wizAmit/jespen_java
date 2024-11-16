package io.jespen.lib;

import com.fasterxml.jackson.annotation.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

// @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record EchoRes(int msg_id, int reply_to, String echo) implements ResPayload {

    public EchoRes(EchoReqPd echoReq, int msg_id) {
        this(msg_id, echoReq.getMsgId(), echoReq.echo());
    }

    @JsonIgnore
    @Override
    public int getInReplyTo() {
        return reply_to();
    }

    @JsonIgnore
    @Override
    public int getMsgId() {
        return msg_id();
    }

    @JsonProperty("type")
    @Override
    public MsgType getMsgType() {
        return MsgType.echo_ok;
    }

    // private static class EchoResProxy implements Serializable {
    //     private static final long serialVersionUID = 838325273185431754L;

    //     private String reply_to;
    //     private String type;
    //     private String msg_id;

    //     public EchoResProxy(EchoRes e) {
    //         this.reply_to = String.valueOf( e.reply_to );
    //         this.type = e.getMsgType().toString();
    //         this.msg_id = String.valueOf( e.msg_id );
    //     }
    // }

    // private void writeObject(ObjectOutputStream oos) throws IOException{
	// 	oos.defaultWriteObject();
		
	// 	oos.writeInt(getMsgId());
    //     oos.writeObject();
	// }

    // private Object writeReplace() {
    //     return new EchoResProxy(this);
    // }

}
