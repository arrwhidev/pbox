package com.arrwhi.pbox.client.filesystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Standalone observable class to watch the file system.
 * Register observers to handle file system change events.
 */
public class FileSystemWatcher extends Observable implements Runnable {

    // TODO: make the poll time customisable from properties.
    private final static int SLEEP_TIME_IN_MILLIS = 2000;

    private Logger logger = LogManager.getLogger();
    private Path rootDir;
    private WatchService watcher;
    private List<DirBeingWatched> directoriesBeingWatched;
    private boolean isWatching;

    public FileSystemWatcher(String rootDir) throws IOException {
        this.isWatching = false;
        this.rootDir = Paths.get(rootDir);
        this.directoriesBeingWatched = new ArrayList<>();
        this.watcher = FileSystems.getDefault().newWatchService();
    }

    public void stop() {
        this.isWatching = false;
    }

    public void run() {
        register(rootDir.toFile());
        for(File f: allNestedDirectoriesForPath(rootDir.toFile())) {
            register(f);
        }

        List<DirWatchEvent> events = new ArrayList<>();
        isWatching = true;
        while(isWatching) {
            for(DirBeingWatched dir : directoriesBeingWatched) {
                for(WatchEvent<?> e : dir.getKey().pollEvents()) {
                    DirWatchEvent event = new DirWatchEvent(e.kind(), Paths.get(dir.getPath().toString(), ((WatchEvent<Path>)e).context().toString()));
                    events.add(event);
                }
            }

            logger.info("Num changes found: " + events.size());

            for (DirWatchEvent event : events) {
                Kind<?> kind = event.getKind();
                if(kind == OVERFLOW) {
                    logger.error("FileSystemWatcher - OVERFLOW");
                    continue;
                } else if (event.getKind() == ENTRY_CREATE || event.getKind() == ENTRY_MODIFY || event.getKind() == ENTRY_DELETE) {
                    if (event.getKind().equals(ENTRY_CREATE) && event.getPath().toFile().isDirectory()) {
                        register(event.getPath().toFile());
                    } else if (event.getKind().equals(ENTRY_DELETE) && event.getPath().toFile().isDirectory()) {
                        // TODO: unregister() - need to remove from `directoriesBeingWatched`.
                        // Seems to be a weird bug here (on linux, need to test on OSX).
                        // event.path.toFile().isDirectory() returns false when it is a directory.
                        // Only happens when deleting a directory, creation works fine - weird!
                    }

                    setChanged();
                    notifyObservers(new FileSystemChangeEvent(event));
                }
            }

            events.clear();

            for(DirBeingWatched dir : directoriesBeingWatched) {
                dir.getKey().reset();
            }

            try {
                Thread.sleep(SLEEP_TIME_IN_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Register directories to watch.
     */
    private void register(File f) {
        if (!f.isDirectory()) {
            logger.info(String.format("Not registering [%s] because it is not a directory.", f.getAbsoluteFile()));
            return;
        } else {
            logger.info(String.format("Registering directory [%s]", f.getAbsoluteFile()));
        }

        Path p = f.toPath();
        try {
            DirBeingWatched dirToWatch = new DirBeingWatched( p.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY), p);
            directoriesBeingWatched.add(dirToWatch);
        } catch (IOException e) {
            logger.error("Failed to register " + f.getAbsolutePath());
            e.printStackTrace();
        }
    }

    private Set<File> allNestedDirectoriesForPath(File directory) {
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
}