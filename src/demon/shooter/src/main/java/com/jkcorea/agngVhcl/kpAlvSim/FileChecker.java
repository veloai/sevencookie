package demon.shooter.src.main.java.com.jkcorea.agngVhcl.kpAlvSim;

import com.credif.util.props.Props;
import com.credif.util.props.PropsException;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;

import java.io.File;
import java.nio.file.attribute.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *  https://drive.google.com/drive/folders/1r69WdDnCta3Riz1L2DfE3UYCF1vF-V6-?usp=sharing
 *
 */
public class FileChecker {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;
    private final String cname;

    static Props props;

    /**
     * Creates a WatchService and registers the given directory
     */
    FileChecker(String cname ) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.cname = cname;
        Path dir = Paths.get(props.getString("watching.dir"));
        System.out.println("Waching directory[" + dir + "]");
        System.out.println("watching.dir.file.remove[" + props.getString("watching.dir.file.remove") + "]");
        registerDirectory(dir);
    }
 
    /**
     * Register the given directory with the WatchService; This function will be called by FileVisitor
     */
    private void registerDirectory(Path dir) throws IOException 
    {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }
 
    /**
     * Register the given directory, and all its sub-directories, with the WatchService.
     */
    private void walkAndRegisterDirectories(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
 
    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        for (;;) {
 
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
 
            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }
 
            for (WatchEvent<?> event : key.pollEvents()) {
                @SuppressWarnings("rawtypes")
                WatchEvent.Kind kind = event.kind();
 
                // Context for directory entry event is the file name of entry
                @SuppressWarnings("unchecked")
                Path name = ((WatchEvent<Path>)event).context();
                Path child = dir.resolve(name);
 
                // print out event
                System.out.format("%s: %s\n", event.kind().name(), child);
                // if directory is created, and watching recursively, then register it and its sub-directories
                if (kind == ENTRY_CREATE || (kind == ENTRY_MODIFY && new File(child.toAbsolutePath().toString()).length() > 0)) {

                    try {
                        Thread.sleep(props.getInt("watching.action.delay"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Shooter.processShooter(child.toAbsolutePath().toString(), props);
//                if (kind == ENTRY_CREATE) {
//                    SingleSenderMain.sendOne(cname, child.toAbsolutePath().toString());
                    /*
                    try {
                        if (Files.isDirectory(child)) {
                            walkAndRegisterDirectories(child);
                        }
                    } catch (IOException x) {
                        // do something useful
                    }
                       */
                }
            }
 
            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
 
                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }
 
    public static void main(String[] args) throws IOException {
//        final 	String 		cname		= args.length <= 0 || args[0] == null || args[0].isEmpty() ? null : args[0];
        String cname = args[0];
//        Path dir = Paths.get(args[1]== null ? "C:\\was\\mecar\\snd\\08" : args[1]);
        try {
            props = new Props(cname == null ? "./kpAlvShooter.properties" : cname);
        } catch (PropsException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        new FileChecker(cname).processEvents();
    }
}