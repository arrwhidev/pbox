package com.arrwhi.pbox.integration;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.util.concurrent.CountDownLatch;

/**
 * Created by arran on 29/01/17.
 */
public class WaitingChannelFutureListener implements ChannelFutureListener {

    private CountDownLatch latch;

    WaitingChannelFutureListener(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void operationComplete(ChannelFuture future) {
        System.out.println("Client connected to server.");
        latch.countDown();
    }
}