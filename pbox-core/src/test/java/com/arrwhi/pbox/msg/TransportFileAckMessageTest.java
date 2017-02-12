package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.exception.InvalidMessageTypeException;
import com.arrwhi.pbox.msg.flags.Flags;
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
        assertEquals(msg.getFlags().getFlags(), 13);
    }

    @Test
    public void writeTo() throws Exception {
        TransportFileAckMessage msg = new TransportFileAckMessage();
        msg.setFlags(new Flags((byte) 17));
        ByteBuf buf = Unpooled.buffer();
        msg.writeToNewBuffer(buf);

        assertEquals(MessageFactory.TRANSPORT_FILE_ACK, buf.readShort());
        assertEquals(17, buf.readByte());
    }

    private ByteBuf createValidPayloadAckMessageBuffer() {
        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(MessageFactory.TRANSPORT_FILE_ACK);
        buf.writeByte((byte) 13); // random byte to represent flags.
        return buf;
    }
}
