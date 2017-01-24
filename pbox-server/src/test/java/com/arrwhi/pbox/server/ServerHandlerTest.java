package com.arrwhi.pbox.server;

import com.arrwhi.pbox.RandomTestUtils;
import com.arrwhi.pbox.json.MetaData;
import com.arrwhi.pbox.msg.TransportFileMessage;
import io.netty.buffer.ByteBuf;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by arran on 21/01/2017.
 */
public class ServerHandlerTest {

    private static final String NAME = "test";
    private FileWriter writer;

    @Before
    public void setup() {
        writer = mock(FileWriter.class);
    }

    @Test
    public void shouldCreateDir_whenTransportFileMessageHasZeroLengthPayload() throws Exception {
        ServerHandler handler = new ServerHandler(writer);
        handler.handleTransportFile(transportFileMessageWithPayload(new byte[0]));
        verify(writer, times(1)).createDir(NAME);
    }

    @Test
    public void shouldCreateFile_whenTransportFileMessageHasZeroLengthPayload() throws Exception {
        final byte[] payload = RandomTestUtils.randomBytes(16);
        ServerHandler handler = new ServerHandler(writer);
        handler.handleTransportFile(transportFileMessageWithPayload(payload));
        verify(writer, times(1)).write(NAME, payload);
    }

    private ByteBuf transportFileMessageWithPayload(byte[] payload) {
        MetaData md = new MetaData();
        md.setTo(NAME);
        byte[] metadata = MetaData.toJsonBytes(md);
        return new TransportFileMessage(metadata, payload).writeTo();
    }
}
