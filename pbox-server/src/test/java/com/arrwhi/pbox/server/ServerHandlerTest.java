package com.arrwhi.pbox.server;

import com.arrwhi.pbox.RandomTestUtils;
import com.arrwhi.pbox.json.MetaData;
import com.arrwhi.pbox.msg.MetaDataBuilder;
import com.arrwhi.pbox.msg.TransportFileMessage;
import com.arrwhi.pbox.msg.flags.Flags;
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
    public void shouldCreateDir_whenTransportFileMessageIsDirectory() throws Exception {
        ServerHandler handler = new ServerHandler(writer);
        Flags flags = new Flags();
        flags.setIsDirectory(true);

        handler.handleTransportFile(transportFileMessageWithPayload(new byte[0], flags), null);
        verify(writer, times(1)).createDir(NAME);
    }

    @Test
    public void shouldCreateFile_whenTransportFileMessageIsNotDirectory_andHasZeroLengthPayload() throws Exception {
        ServerHandler handler = new ServerHandler(writer);
        final byte[] payload = RandomTestUtils.randomBytes(0);
        handler.handleTransportFile(transportFileMessageWithPayload(payload, new Flags()), null);
        verify(writer, times(1)).write(NAME, payload);
    }

    @Test
    public void shouldCreateFile_whenTransportFileMessageIsNotDirectory() throws Exception {
        final byte[] payload = RandomTestUtils.randomBytes(16);
        ServerHandler handler = new ServerHandler(writer);
        handler.handleTransportFile(transportFileMessageWithPayload(payload, new Flags()), null);
        verify(writer, times(1)).write(NAME, payload);
    }

    private ByteBuf transportFileMessageWithPayload(byte[] payload, Flags flags) {
        MetaData md = new MetaDataBuilder().withTo(NAME).build();
        TransportFileMessage m = new TransportFileMessage(md, payload);
        m.setFlags(flags);

        return m.writeToNewBuffer();
    }
}
