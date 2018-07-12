package com.arrwhi.pbox.netty;

import com.arrwhi.pbox.msg.MessageType;
import io.netty.util.AttributeKey;

public class PipelineAttributes {

    public static final AttributeKey<MessageType> MESSAGE_TYPE = AttributeKey.valueOf("msg_type");

}
