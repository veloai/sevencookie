package swing.demon.cmrctl;

import swing.demon.util.props.Props;
import swing.demon.util.props.PropsException;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

public class CmrctlMain {

    static Props props;
    private WatchService watchService;
    private Map<WatchKey,Path> keys;
    private WatchKey watchKey;

    static String[] dvcIps = null;		//제어기 아이피
    static String[] dvcIds = null;		//제어기 아이디

    public void cmrctl (String propPath) {
        System.out.println("##########   START Cookie  ##########");
        long startlogT = System.currentTimeMillis();
        //prop 호출
        try {
            props = new Props(propPath);
        } catch (PropsException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /* option */
        //실시간 파일 검지 서비스 정의 --기존 처리 내용
        //watchService = FileSystems.getDefault().newWatchService();
        //keys = new HashMap<WatchKey, Path>();

        //실시간 파일 검지 위치 설정 -- 기존 처리 내용
        //Path dir = Paths.get(props.getString("watching.dir"));
        //System.out.println("Waching directory:[" + dir + "]");


        //파일 이벤트 정보 입력 -- 기존 처리 내용
        //WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        //keys.put(key, dir);

        Path path = Paths.get(props.getString("watching.dir"));
        System.out.println(path);
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            //해당 디렉토리 경로에 와치서비스와 이벤트 등록
            path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.OVERFLOW);

            Thread thread = new Thread(()->{
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        watchKey = watchService.take();//이벤트가 오길 대기(Blocking)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    List<WatchEvent<?>> events = watchKey.pollEvents();//이벤트들을 가져옴
                    for(WatchEvent<?> event : events) {
                        //이벤트 종류
                        WatchEvent.Kind<?> kind = event.kind();
                        //경로
                        Path fileName = (Path)event.context();
                        if(kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                            System.out.println("created something in directory");
                            ControlCmr.processCtrl(path+File.separator+fileName, props);
                        }else if(kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                            System.out.println("delete something in directory");
                        }else if(kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                            System.out.println("modified something in directory");
                        }else if(kind.equals(StandardWatchEventKinds.OVERFLOW)) {
                            System.out.println("overflow");
                        }else {
                            System.out.println("hello world");
                        }
                    }
                    if(!watchKey.reset()) {
                        try {
                            watchService.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.setName("Thread watch");
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // #######################################
        long endLogt = System.currentTimeMillis();
        long logt = endLogt - startlogT;
        System.out.println("걸린 시간: " + logt + " 밀리초");

    }
}

