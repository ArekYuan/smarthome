package cn.interlinx.iot;

import org.springframework.scheduling.annotation.Scheduled;

public class SocketService {

    public static void start() {
        Thread server = new Thread(new Server(8888));
        try {
            server.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        server.start();
    }
}
