package com.arrwhi.pbox.client.io;

import org.junit.Test;

import static org.junit.Assert.assertNull;

public class MessageQueueTest {

    private final static String FILE  = "/home/arran/pbox/foo";
//    private MessageQueue q = new MessageQueue();

//    @Test
//    public void offeredFileIsSameAsPolledFile() throws Exception {
//        q.offer(new File(FILE));
//        assertEquals(FILE, q.poll().toString());
//    }

    @Test
    public void pollingAnEmptyQueueReturnsNull() throws Exception {
//        assertNull(q.poll());
    }
}
