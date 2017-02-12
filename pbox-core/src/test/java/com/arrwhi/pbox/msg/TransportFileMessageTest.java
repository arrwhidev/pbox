package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.exception.InvalidMessageTypeException;
import com.arrwhi.pbox.msg.flags.Flags;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import static com.arrwhi.pbox.RandomTestUtils.randomBytes;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TransportFileMessageTest {

    @Test(expected=InvalidMessageTypeException.class)
    public void shouldThrowException_whenInvalidMessageType() throws Exception {
        ByteBuf source = Unpooled.buffer();
        source.writeShort(999);
        MessageFactory.createTransportFileMessageFrom(source);
        fail("Should have thrown InvalidMessageTypeException.");
    }

    @Test
    public void readFrom() throws Exception {
        byte[] metadata = randomBytes(16);
        byte[] payload = randomBytes(1024);
        ByteBuf source = createValidPayloadMessageBuffer(metadata, payload);
        TransportFileMessage msg = MessageFactory.createTransportFileMessageFrom(source);
        assertArrayEquals(metadata, msg.getMetaData());
        assertArrayEquals(payload, msg.getPayload());
        assertEquals(msg.getType(), MessageFactory.TRANSPORT_FILE);
        assertEquals(msg.getFlags().getFlags(), 13);
    }

    @Test
    public void writeTo() throws Exception {
        byte[] metadata = randomBytes(16);
        byte[] payload = randomBytes(1024);
        TransportFileMessage msg = new TransportFileMessage();
        msg.setFlags(new Flags((byte) 17));
        msg.setMetaData(metadata);
        msg.setPayload(payload);
        ByteBuf buf = Unpooled.buffer();
        msg.writeToNewBuffer(buf);

        assertEquals(MessageFactory.TRANSPORT_FILE, buf.readShort());
        assertEquals(17, buf.readByte());
        buf.readInt();
        byte[] newMetaData = new byte[16];
        buf.readBytes(newMetaData);
        byte[] newPayload = new byte[1024];
        buf.readBytes(newPayload);
        assertArrayEquals(metadata, newMetaData);
        assertArrayEquals(payload,newPayload);
        assertEquals(0, buf.readableBytes());
    }

    private ByteBuf createValidPayloadMessageBuffer(byte[] metadata, byte[] payload) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(MessageFactory.TRANSPORT_FILE);
        buf.writeByte((byte) 13); // random byte to represent flags.
        buf.writeInt(metadata.length);
        buf.writeBytes(metadata);
        buf.writeBytes(payload);
        return buf;
    }
}
