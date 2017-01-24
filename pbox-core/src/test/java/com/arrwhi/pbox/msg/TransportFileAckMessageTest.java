package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.exception.InvalidMessageTypeException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TransportFileAckMessageTest {

    @Test(expected=InvalidMessageTypeException.class)
    public void shouldThrowException_whenInvalidMessageType() throws Exception {
        ByteBuf source = Unpooled.buffer();
        source.writeShort(999);
        MessageFactory.createTransportFileAckMessageFrom(source);
        fail("Should have thrown InvalidMessageTypeException.");
    }

    @Test
    public void readFrom() throws Exception {
        ByteBuf source = createValidPayloadAckMessageBuffer();
        TransportFileAckMessage msg = MessageFactory.createTransportFileAckMessageFrom(source);
        assertEquals(msg.getType(), MessageFactory.TRANSPORT_FILE_ACK);
    }

    @Test
    public void writeTo() throws Exception {
        TransportFileAckMessage msg = new TransportFileAckMessage();
        ByteBuf buf = Unpooled.buffer();
        msg.writeTo(buf);

        assertEquals(MessageFactory.TRANSPORT_FILE_ACK, buf.readShort());
    }

    private ByteBuf createValidPayloadAckMessageBuffer() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(MessageFactory.TRANSPORT_FILE_ACK);
        return buf;
    }
}
