package com.arrwhi.pbox.client.io;

import java.util.Observable;

/**
 * Created by arran on 15/01/2017.
 */
// TODO - do something with this.
public class FileSystemEventObserver implements java.util.Observer {

    @Override
    public void update(Observable o, Object arg) {
        String s = (String) arg;
        System.out.println("UPDATE! " + s);
    }
}
