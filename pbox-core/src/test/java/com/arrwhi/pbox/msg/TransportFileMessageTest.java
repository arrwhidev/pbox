package com.arrwhi.pbox.msg;

import com.arrwhi.pbox.exception.InvalidMessageTypeException;
import com.arrwhi.pbox.msg.flags.Flags;
import com.arrwhi.pbox.msg.metadata.MetaData;
import com.arrwhi.pbox.msg.metadata.MetaDataBuilder;
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
        MessageFactory.fromBuffer(source, MessageType.TRANSPORT_FILE);
        fail("Should have thrown InvalidMessageTypeException.");
    }

    @Test
    public void readFrom() throws Exception {
        byte[] metadata = "{}".getBytes();
        byte[] payload = randomBytes(1024);
        ByteBuf source = createValidPayloadMessageBuffer(metadata, payload);
        TransportFileMessage msg = (TransportFileMessage) MessageFactory.fromBuffer(source, MessageType.TRANSPORT_FILE);
        assertArrayEquals(metadata, msg.getMetaData().toJsonBytes());
        assertArrayEquals(payload, msg.getPayload());
        assertEquals(msg.getType(), MessageType.TRANSPORT_FILE);
        assertEquals(msg.getFlags().getFlags(), 13);
    }

    @Test
    public void writeTo() throws Exception {
        MetaData metadata = new MetaDataBuilder().withFrom("from").withHash("hash").withTo("to").build();
        byte[] payload = randomBytes(1024);
        TransportFileMessage msg = new TransportFileMessage();
        msg.setFlags(new Flags((byte) 17));
        msg.setMetaData(metadata);
        msg.setPayload(payload);
        ByteBuf buf = Unpooled.buffer();
        msg.writeToNewBuffer(buf);

        assertEquals(MessageType.TRANSPORT_FILE.getType(), buf.readShort());
        assertEquals(17, buf.readByte());
        buf.readInt();
        byte[] newMetaData = new byte[metadata.toJsonBytes().length];
        buf.readBytes(newMetaData);
        byte[] newPayload = new byte[1024];
        buf.readBytes(newPayload);
        assertArrayEquals(payload, newPayload);
        assertEquals(0, buf.readableBytes());

        MetaData metadata2 = MetaData.fromJsonBytes(newMetaData);
        assertEquals(metadata.getFrom(), metadata2.getFrom());
        assertEquals(metadata.getTo(), metadata2.getTo());
        assertEquals(metadata.getHash(), metadata2.getHash());
    }

    private ByteBuf createValidPayloadMessageBuffer(byte[] metadata, byte[] payload) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeShort(MessageType.TRANSPORT_FILE.getType());
        buf.writeByte((byte) 13); // random byte to represent flags.
        buf.writeInt(metadata.length);
        buf.writeBytes(metadata);
        buf.writeBytes(payload);
        return buf;
    }
}
