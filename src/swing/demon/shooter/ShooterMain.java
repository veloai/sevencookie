package swing.demon.shooter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swing.demon.util.ExceptionConvert;
import swing.demon.util.props.Props;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

public class ShooterMain {
    static Props props;
    private WatchService watchService;
    private Map<WatchKey, Path> keys;
    private WatchKey watchKey;

    static Thread thread = null;
    boolean isLogShow = false;
    private static Logger logger = LoggerFactory.getLogger(ShooterMain.class);

    public void shooterStart (String propPath) {
        //prop 호출
        props = new Props(propPath);

        isLogShow = props.getBoolean("is.log.show");

        if(thread == null) {

            Path path = Paths.get(props.getString("watching.dir"));
            //System.out.println(path);
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                //해당 디렉토리 경로에 와치서비스와 이벤트 등록
                path.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_MODIFY);

                thread = new Thread(()->{
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            watchKey = watchService.take();//이벤트가 오길 대기(Blocking)
                        } catch (InterruptedException e) {
                            logger.info(ExceptionConvert.TraceAllError(e));
                            try {
                                watchService.close();
                            } catch (IOException de) {
                                logger.info(ExceptionConvert.TraceAllError(de));
                            }
                        }
                        List<WatchEvent<?>> events = watchKey.pollEvents();//이벤트들을 가져옴
                        for(WatchEvent<?> event : events) {
                            //이벤트 종류
                            WatchEvent.Kind<?> kind = event.kind();
                            //경로
                            Path fileName = (Path)event.context();
                            if(kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                                //System.out.println("created something in directory thread id : "+thread.getId());
                                Shooter.processShooter(path+ File.separator+fileName, props, isLogShow);
                            }else if(kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                                //System.out.println("modified something in directory");
                            }else {
                                //System.out.println("hello world");
                            }
                        }
                        if(!watchKey.reset()) {
                            try {
                                watchService.close();
                            } catch (IOException e) {
                                logger.info(ExceptionConvert.TraceAllError(e));
                            }
                        }
                    }
                });
                thread.setName("Thread shooter");
                thread.start();
            } catch (IOException e) {
                logger.info(ExceptionConvert.TraceAllError(e));
            }
        } else {
            logger.info("이미 실행중 입니다.");
        }
    }

    public void shooterStop () {
        if(thread != null) {
            thread.interrupt();
            thread = null;
            logger.info("정상적으로 Shooter 종료");
        } else {
            logger.info("실행중인 shooter thread 없습니다.");
        }
    }
}
