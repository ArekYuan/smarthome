package cn.interlinx.iot;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class ApplicationStartup implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger log = Logger.getLogger(ApplicationStartup.class.getSimpleName());


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // 初始化完成后. 启动Socket
//        Thread server = new Thread(new Server(8888));
//        server.start();
        SocketService.start();
        log.info("socket :{}--port--->" + 8888);



    }

}
