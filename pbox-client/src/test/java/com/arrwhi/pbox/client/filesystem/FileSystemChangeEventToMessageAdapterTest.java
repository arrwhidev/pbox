package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.client.adapters.FileSystemChangeEventToMessageAdapter;
import com.arrwhi.pbox.msg.DeleteFileMessage;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.msg.TransportFileMessage;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by arran on 14/01/17.
 */
public class FileSystemChangeEventToMessageAdapterTest {

    final static String ROOT_DIR = "/home/arran";

    private FileSystemChangeEventToMessageAdapter adapter;

    @Before
    public void setup() {
        adapter = new FileSystemChangeEventToMessageAdapter(ROOT_DIR);
    }

    @Test
    public void shouldReturnTransportFileMessage_whenCreateEvent_andIsNotDirectory() throws Exception {
        FileSystemChangeEvent ev = FileSystemChangeEventFactory.fsCreateEvent(ROOT_DIR + "/testing/hello.gif", false);
        Message msg = adapter.adapt(ev);
        assertThat(msg, is(instanceOf(TransportFileMessage.class)));
    }

    @Test
    public void shouldReturnTransportFileMessageWithZeroBytePayload_whenCreateEvent_andIsDirectory() throws Exception {
        FileSystemChangeEvent ev = FileSystemChangeEventFactory.fsCreateEvent(ROOT_DIR + "/testing", true);
        Message msg = adapter.adapt(ev);
        assertThat(msg, is(instanceOf(TransportFileMessage.class)));
        assertThat(((TransportFileMessage) msg).getPayload().length, is(0));
    }

    @Test
    public void shouldReturnNull_whenModifyEvent() throws Exception {
        FileSystemChangeEvent ev = FileSystemChangeEventFactory.fsModifyEvent(ROOT_DIR + "/testing", true);
        Message msg = adapter.adapt(ev);
        assertThat(msg, is(nullValue()));
    }

    @Test
    public void shouldReturnNull_whenDeleteEvent_andIsDirectory() throws Exception {
        FileSystemChangeEvent ev = FileSystemChangeEventFactory.fsDeleteEvent(ROOT_DIR + "/testing", true);
        Message msg = adapter.adapt(ev);
        assertThat(msg, is(nullValue()));
    }

    @Test
    public void shouldReturnDeleteFileMessage_whenDeleteEvent_andIsDirectory() throws Exception {
        FileSystemChangeEvent ev = FileSystemChangeEventFactory.fsDeleteEvent(ROOT_DIR + "/testing", false);
        Message msg = adapter.adapt(ev);
        assertThat(msg, is(instanceOf(DeleteFileMessage.class)));
    }
}
