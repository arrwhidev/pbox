package com.arrwhi.pbox.client.filesystem;

import com.arrwhi.pbox.client.adapters.FileSystemEventToMessageAdapter;
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

    private Logger logger = LogManager.getLogger();
    private Path rootDir;
    private WatchService watcher;
    private List<DirBeingWatched> directoriesBeingWatched;
    private FileSystemEventToMessageAdapter eventToMessageAdapter;
    private boolean isWatching;

    public FileSystemWatcher(String rootDir) throws IOException {
        this.isWatching = false;
        this.rootDir = Paths.get(rootDir);
        this.directoriesBeingWatched = new ArrayList<>();
        this.eventToMessageAdapter = new FileSystemEventToMessageAdapter(rootDir);
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

            logger.info("Num changes found: " + events.size());
            for (DirWatchEvent event : events) {
                Kind<?> kind = event.kind;
                if(kind == OVERFLOW) {
                    logger.error("FileSystemWatcher - OVERFLOW");
                    continue;
                } else {
                    // Seems to be a weird bug here (on linux, need to test on OSX).
                    // event.path.toFile().isDirectory() returns false when it is a directory.
                    logger.info(event.kind.name() + " - isDirectory:" + event.path.toFile().isDirectory());

                    if (event.kind == ENTRY_CREATE || event.kind == ENTRY_MODIFY || event.kind == ENTRY_DELETE) {
                        if (event.kind.equals(ENTRY_CREATE) && event.path.toFile().isDirectory()) {
                            register(event.path.toFile());
                        } else if (event.kind.equals(ENTRY_DELETE) && event.path.toFile().isDirectory()) {
                            // TODO: unregister() - need to remove from dirsToWatch.
                        }

                        setChanged();
                        notifyObservers(new FileSystemChangeEvent(event));
                    }
                }
            }

            for(DirBeingWatched dir : directoriesBeingWatched) {
                dir.key.reset();
            }

            try {
                // TODO: make the poll time customisable from properties.
                Thread.sleep(2000);
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
            DirBeingWatched dirToWatch = new DirBeingWatched();
            dirToWatch.key = p.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            dirToWatch.path = p;
            directoriesBeingWatched.add(dirToWatch);
        } catch (IOException e) {
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