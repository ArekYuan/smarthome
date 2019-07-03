package cn.interlinx.iot;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

public class SocketThread  extends Thread implements InitializingBean {
    private Logger log = Logger.getLogger(SocketThread.class.getSimpleName());

//    @Autowired
//    SocketService service;

//    private Server server;

    @Override
    public void run() {
        log.info("当前线程名：{}"+ Thread.currentThread().getName());
        log.info("由当前线程开始启动Socket服务...");
//        SocketService.start();
//        new Server().run();

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}
