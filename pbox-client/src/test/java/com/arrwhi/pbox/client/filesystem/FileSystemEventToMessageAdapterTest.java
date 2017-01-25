package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.client.adapters.FileSystemEventToMessageAdapter;
import com.arrwhi.pbox.msg.DeleteFileMessage;
import com.arrwhi.pbox.msg.Message;
import com.arrwhi.pbox.msg.TransportFileMessage;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.junit.MatcherAssert.assertThat;

/**
 * Created by arran on 14/01/17.
 */
public class FileSystemEventToMessageAdapterTest {

    final static String ROOT_DIR = "/home/arran";

    @Test
    public void shouldReturnTransportFileMessage_whenCreateEvent_andIsNotDirectory() throws Exception {
        DirWatchEvent ev = FileSystemEventFactory.fsCreateEvent(ROOT_DIR + "/testing/hello.gif", false);
        Message msg = new FileSystemEventToMessageAdapter(ROOT_DIR).adapt(ev);
        assertThat(msg, is(instanceOf(TransportFileMessage.class)));
    }

    @Test
    public void shouldReturnTransportFileMessageWithZeroBytePayload_whenCreateEvent_andIsDirectory() throws Exception {
        DirWatchEvent ev = FileSystemEventFactory.fsCreateEvent(ROOT_DIR + "/testing", true);
        Message msg = new FileSystemEventToMessageAdapter(ROOT_DIR).adapt(ev);
        assertThat(msg, is(instanceOf(TransportFileMessage.class)));
        assertThat(((TransportFileMessage) msg).getPayload().length, is(0));
    }

    @Test
    public void shouldReturnNull_whenModifyEvent() throws Exception {
        DirWatchEvent ev = FileSystemEventFactory.fsModifyEvent(ROOT_DIR + "/testing", true);
        Message msg = new FileSystemEventToMessageAdapter(ROOT_DIR).adapt(ev);
        assertThat(msg, is(nullValue()));
    }

    @Test
    public void shouldReturnNull_whenDeleteEvent_andIsDirectory() throws Exception {
        DirWatchEvent ev = FileSystemEventFactory.fsDeleteEvent(ROOT_DIR + "/testing", true);
        Message msg = new FileSystemEventToMessageAdapter(ROOT_DIR).adapt(ev);
        assertThat(msg, is(nullValue()));
    }

    @Test
    public void shouldReturnDeleteFileMessage_whenDeleteEvent_andIsDirectory() throws Exception {
        DirWatchEvent ev = FileSystemEventFactory.fsDeleteEvent(ROOT_DIR + "/testing", false);
        Message msg = new FileSystemEventToMessageAdapter(ROOT_DIR).adapt(ev);
        assertThat(msg, is(instanceOf(DeleteFileMessage.class)));
    }
}
