//package com.arrwhi.pbox.client.io;
//
//import com.arrwhi.pbox.msg.Message;
//
//import java.util.LinkedList;
//
//public class MessageQueue {
//
//    private LinkedList<Message> msgs = new LinkedList<>();
//
//    public synchronized void offer(Message msg) {
//        msgs.offer(msg);
//    }
//
//    public synchronized Message poll() {
//        return msgs.poll();
//    }
//}
