package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.client.adapters.FileSystemEventToMessageAdapter;
import com.arrwhi.pbox.msg.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileSystemWatcher extends Observable implements Runnable {

    private Path rootDir;
    private WatchService watcher;
    private List<DirBeingWatched> directoriesBeingWatched;
    private FileSystemEventToMessageAdapter eventToMessageAdapter;
    private boolean isWatching;

    public FileSystemWatcher(String rootDir, WatchService watcher, FileSystemEventToMessageAdapter adapter) {
        this.rootDir = Paths.get(rootDir);
        this.watcher = watcher;
        this.directoriesBeingWatched = new ArrayList<>();
        this.eventToMessageAdapter = adapter;
        this.isWatching = false;
    }

    public void stop() {
        this.isWatching = false;
    }

    void register(File f) {
        Path p = f.toPath();
        try {
            DirBeingWatched dirToWatch = new DirBeingWatched();
            dirToWatch.key = p.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            dirToWatch.path = p;
            directoriesBeingWatched.add(dirToWatch);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Set<File> allNestedDirectoriesForPath(File directory) {
        return allNestedDirectoriesForPath(new HashSet<>(), directory);
    }

    Set<File> allNestedDirectoriesForPath(Set<File> dirs, File directory) {
        for(File f : directory.listFiles()) {
            if(f.isDirectory()) {
                dirs.add(f);
                if(f.listFiles().length > 0) {
                    allNestedDirectoriesForPath(dirs, f);
                }
            }
        }
        return dirs;
    }

    public void run() {
        register(rootDir.toFile());
        for(File f: allNestedDirectoriesForPath(rootDir.toFile())) {
            register(f);
        }

        isWatching = true;
        while(isWatching) {
            List<DirWatchEvent> events = new ArrayList<>();
            for(DirBeingWatched dir : directoriesBeingWatched) {
                for(WatchEvent<?> e : dir.key.pollEvents()) {
                    DirWatchEvent event = new DirWatchEvent();
                    event.kind = e.kind();
                    event.path = Paths.get(dir.path.toString(), ((WatchEvent<Path>)e).context().toString());
                    events.add(event);
                }
            }

            System.out.println("----\nNum changes found: " + events.size());
            for (DirWatchEvent event : events) {
                Kind<?> kind = event.kind;
                if(kind == OVERFLOW) {
                    System.err.println("FileSystemWatcher - OVERFLOW");
                    continue;
                } else {
                    Message msg = eventToMessageAdapter.adapt(event);
                    if (msg != null) {
                        if (event.kind.equals(ENTRY_CREATE) && event.path.toFile().isDirectory()) {
                            register(event.path.toFile());
                        }

                        setChanged();
                        notifyObservers(new FileSystemChangeEvent(event, msg));
                    }
                }
            }

            for(DirBeingWatched dir : directoriesBeingWatched) {
                dir.key.reset();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.err.println("Interrupted!");
                e.printStackTrace();
            }
        }
    }
}