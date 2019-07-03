package cn.interlinx.iot;

import cn.interlinx.controller.device.DeviceController;
import cn.interlinx.utils.util.HexUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static cn.interlinx.utils.consts.Constant.SOCKET_TIME_OUT;

public class Server implements Runnable {
    private int port;
    private volatile boolean stop;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private SelectionKey selectionKey;

    private Logger log = Logger.getLogger(Server.class.getSimpleName());

    @Autowired
    DeviceController deviceController;

    public Server(int port) {
        this.port = port;
    }

    public void init() {
        try {
            //打开一个选择器
            selector = Selector.open();
            //打开一个Server-Socket监听通道
            serverSocketChannel = ServerSocketChannel.open();
            //设置该通道为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            //将通道注册在选择器上面，并将准备连接状态作为通道订阅时间
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            stop = false;
            System.out.println("服务器已经启动，端口号：" + port);
            log.info("服务器已经启动，端口号" + port);
        } catch (IOException e) {
            e.printStackTrace();
            log.finest("socket--开启异常-->" + e.getMessage());
        }
    }

    public void run() {
        init();
        while (!stop) {
            try {
                //无论是否有读写事件发生，selector每隔10s被唤醒一次
                selector.select(SOCKET_TIME_OUT);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                String expression = "";
                while (iterator.hasNext()) {
                    selectionKey = iterator.next();
                    //判断是否准备好接收新进入的连接
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                        //通过ServerSocketChannel的accept()创建SocketChannel实例
                        //完成该操作意味着完成TCP三次握手，TCP物理链路正式建立
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        //设置为非阻塞
                        socketChannel.configureBlocking(false);
                        //在选择器注册，并订阅读事件
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    if (selectionKey.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        //创建byteBuffer，并开辟一个1M的缓冲区
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        //读取请求码流，返回读取到的字节数
                        int readBytes = socketChannel.read(byteBuffer);
                        //判断客户端是否断开
                        if (readBytes < 0) {
                            selectionKey.cancel();
                            socketChannel.close();
                            return;
                        }
                        //读取到字节，对字节进行编解码
                        if (readBytes > 0) {
                            //将缓冲区从写模式切换到读模式
                            byteBuffer.flip();
                            //根据缓冲区可读字节数创建字节数组
                            byte[] bytes = new byte[byteBuffer.remaining()];
                            //向缓冲区读数据到字节数组
                            byteBuffer.get(bytes);
                            expression = new String(bytes, "UTF-8");
                            List<String> list = HexUtil.bytes2HexString(bytes);
//                            List<String> list1 = HexUtil.bytes2Hex(bytes);
                            String data = initData(list);

                            boolean flag = ChannelPool.channelMap.containsKey(expression);
                            if (!flag) {
                                ChannelClient client = new ChannelClient(expression, socketChannel);
                                ChannelPool.add(client);
                            }
                            if (socketChannel.finishConnect()) {
                                sendMessage(list, expression);
                            }
                        }
                    }
                    iterator.remove();
                }
                selectionKeys.clear();
            } catch (Exception e) {
                e.printStackTrace();
                log.finest("socket--接收異常-->" + e.getMessage());
            }
        }

        //selector关闭后会自动释放里面管理的资源
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 16进制数据
     * @param strList
     * @return
     */
    private String initData(List<String> strList) {
        String str = "";
        if (strList != null && strList.size() > 0) {
            if (strList.get(0).equals("55") && strList.get(1).equals("AA")) {
//                int len = Integer.valueOf(List.get(2));
                int len = Integer.parseInt(strList.get(2), 16);//将16进制数转成10进制数
                if (len == strList.size()) {
                    switch (strList.get(3)) {
                        case "10"://0x10:设备登录到服务器
                            break;
                        case "11"://0x11:设备状态上报
                            break;
                        case "12"://0x12:服务器控制设备状态
                            break;
                        case "13"://0x13:心跳包
                            break;
                    }
                }

            }
        }
        return str;
    }

    private void sendMessage(List<String> strList, String expression) {
        try {
            String resp = "0x11,";

            ChannelClient client = ChannelPool.getChannelClient(expression);
            byte[] req = resp.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
            writeBuffer.put(req);
            writeBuffer.flip();
            client.getChannel().write(writeBuffer);
            if (!writeBuffer.hasRemaining()) {
                System.out.println("向客戶端发送消息：" + resp);
                log.info("向客戶端发送消息：" + resp);
            }else{
                writeBuffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.finest("socket--消息發送異常-->" + e.getMessage());
        }
    }



}
