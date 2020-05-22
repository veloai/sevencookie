package swing.demon.cmrctl.ctrl;
import swing.demon.util.props.Props;
import swing.demon.util.props.PropsException;

import static java.nio.file.StandardWatchEventKinds.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
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
    static String[] dvcIps = null;		//제어기 아이피
    static String[] dvcIds = null;		//제어기 아이디

    /**
     * Creates a WatchService and registers the given directory
     */
    public FileChecker(String cname) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.cname = cname;
        System.out.println(props.getString("watching.dir"));
        Path dir = Paths.get(props.getString("watching.dir"));
        System.out.println("Waching directory:[" + dir + "]");
        dvcIps = props.getString("dvc.ip").trim().split(",");
        dvcIds = props.getString("dvc.id").trim().split(",");
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
            try {
                // wait for key to be signalled
                WatchKey key;
                try {
                    key = watcher.take();
                    System.out.println(key);
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
                    Path name = ((WatchEvent<Path>) event).context();
                    Path child = dir.resolve(name);

                    // print out event
                    // if directory is created, and watching recursively, then register it and its sub-directories
                    if (kind == ENTRY_CREATE) {
                    	System.out.format("%s: %s\n", event.kind().name(), child);

                        try {
                            Thread.sleep(props.getInt("watching.action.delay"));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        ControlOprt.processCtrl(child.toAbsolutePath().toString(), props);
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
             } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
 
    public static void main(String[] args) throws IOException {
//        final 	String 		cname		= args.length <= 0 || args[0] == null || args[0].isEmpty() ? null : args[0];
        String cname = args[0];
//        Path dir = Paths.get(args[1]== null ? "C:\\was\\mecar\\snd\\08" : args[1]);
        try {
            props = new Props(cname == null ? "./control.config.properties" : cname);
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