package com.arrwhi.pbox.client.filesystem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by arran on 15/01/17.
 */
public class FileSystemEventFactory {

    public static DirWatchEvent fsCreateEvent(String filename, boolean isDirectory) {
        return fsEvent(filename, isDirectory, StandardWatchEventKinds.ENTRY_CREATE);
    }

    public static DirWatchEvent fsModifyEvent(String filename, boolean isDirectory) {
        return fsEvent(filename, isDirectory, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public static DirWatchEvent fsDeleteEvent(String filename, boolean isDirectory) {
        return fsEvent(filename, isDirectory, StandardWatchEventKinds.ENTRY_DELETE);
    }

    private static DirWatchEvent fsEvent(String filename, boolean isDirectory, WatchEvent.Kind kind) {
        File mockedFile = mock(File.class);
        when(mockedFile.isDirectory()).thenReturn(isDirectory);
        when(mockedFile.toString()).thenReturn(filename);

        Path mockedPath = mock(Path.class);
        when(mockedPath.toFile()).thenReturn(mockedFile);

        DirWatchEvent ev = new DirWatchEvent();
        ev.kind = kind;
        ev.path = mockedPath;
        return ev;
    }


}
