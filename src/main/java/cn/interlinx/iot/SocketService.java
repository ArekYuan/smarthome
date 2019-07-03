package cn.interlinx.iot;


public class SocketService {

    public static void start() {
        Thread server = new Thread(new Server(8888));
        server.start();
    }
}
