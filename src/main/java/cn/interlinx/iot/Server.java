package cn.interlinx.iot;

import cn.interlinx.controller.device.DeviceController;
import cn.interlinx.entity.Device;
import cn.interlinx.service.intel.DeviceService;
import cn.interlinx.utils.util.HexUtil;
import cn.interlinx.utils.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.persistence.Convert;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
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

    //    @Autowired
    DeviceService service;

//    @Autowired
//    DeviceController controller;

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
            service = SpringUtil.getBean(DeviceService.class);
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
//                            expression = new String(bytes, "UTF-8");
                            List<String> list = HexUtil.bytes2HexString(bytes);


                            List<String> datas = HexUtil.getMcuDataList(list);
                            String[] a = list.toArray(new String[datas.size()]);
                            System.out.println("--mcu-通讯码->" + Arrays.toString(a));
                            log.info("--mcu-通讯码->" + Arrays.toString(a));
                            String sum = getSun(datas);
                            String data = HexUtil.getMcuData(datas);
//                            System.out.println("----sun-->" + sum);
                            String he = datas.get(datas.size() - 1);
                            System.out.println("---he-->" + he);
                            boolean isToken = false;
                            if (he.equals(sum)) {//验证成功
                                isToken = true;
                                switch (data) {
                                    case "0x10"://设备登录到服务器
                                        int chang_id = getChangId(list);
                                        int sheBei_id = getSBId(list);
                                        String mac = getMac(list);
//                                        System.out.println("-厂商-id-->" + chang_id);
//                                        System.out.println("-設備-id-->" + sheBei_id);
//                                        System.out.println("-wifi-mac-->" + mac);
                                        String key = String.valueOf(chang_id + sheBei_id);
                                        Device device = service.selectByKey(key);
                                        if (device != null && device.getWifiMac() != null) {
                                            expression = key;
                                        } else {
                                            expression = key;
                                            Device device1 = new Device();
                                            device1.setWifiMac(mac);
                                            device1.setDeviceKey(key);

                                            int flag = service.insert(device1);
                                            if (flag == 1) {
                                                System.out.println("插入设备成功");
                                            } else {
                                                System.out.println("插入设备失败");
                                            }
                                        }

                                        list.clear();
                                        a = null;
                                        break;
                                    case "0x11"://设备状态上报


                                        list.clear();
                                        a = null;
                                        break;
                                    case "0x12"://服务器控制设备状态

                                        list.clear();
                                        a = null;
                                        break;
                                    case "0x13"://心跳包

                                        list.clear();
                                        a = null;
                                        break;
                                    default://上报失败

                                        list.clear();
                                        a = null;
                                        break;
                                }
                            } else {//验证失败
                                isToken = false;
                                list.clear();
                                a = null;
                            }


//                            log.info("--mcu-哪种状态->" + data);
                            boolean flag = ChannelPool.channelMap.containsKey(expression);
                            if (!flag) {
                                ChannelClient client = new ChannelClient(expression, socketChannel);
                                ChannelPool.add(client);
                            }
                            if (socketChannel.finishConnect()) {
                                list.clear();
                                a = null;
                                sendData(data, expression, isToken);
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
     * 数据域 校验和
     *
     * @param list
     * @return
     */
    private String getSun(List<String> list) {
        int sum = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            sum += Integer.parseInt(list.get(i), 16);
        }
        int mod = sum % 256;
        String hex = Integer.toHexString(mod);
        int len = hex.length();
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex;

    }

    private String getMac(List<String> list) {
        StringBuffer sb = new StringBuffer();
        List<String> list1 = list.subList(13, 19);
        for (int i = 0; i < list1.size(); i++) {
            String str = list1.get(i);
            String lower;
            lower = str.toLowerCase();
            if (i == 0) {
                lower = lower + ":";
                sb.append(lower);
            } else if (i == list1.size() - 1) {
                sb.append(lower);
            } else {
                sb.append(lower + ":");
            }
        }
        return sb.toString();
    }

    /**
     * 获取设备id
     *
     * @param list
     * @return
     */
    private int getSBId(List<String> list) {
        int b2 = Integer.parseInt(list.get(9), 16);//将16进制数转成10进制数
        int b3 = Integer.parseInt(list.get(10), 16);//将16进制数转成10进制数
        int b4 = Integer.parseInt(list.get(11), 16);
        int b5 = Integer.parseInt(list.get(12), 16);
        int id = (int) (b2 << 24 | b3 << 16 | b4 << 8 | b5);
        return id;
    }

    private int getChangId(List<String> strList) {
        int b2 = Integer.parseInt(strList.get(5), 16);//将16进制数转成10进制数
        int b3 = Integer.parseInt(strList.get(6), 16);//将16进制数转成10进制数
        int b4 = Integer.parseInt(strList.get(7), 16);
        int b5 = Integer.parseInt(strList.get(8), 16);
        int id = (int) (b2 << 24 | b3 << 16 | b4 << 8 | b5);
        return id;
    }

    private void sendData(String data, String expression, boolean isToken) {
        switch (data) {
            case "0x10"://0x10:设备登录到服务器
                sendMessage(expression, isToken);
                break;
//            case "0x11"://0x11:设备状态上报
//                break;
//            case "0x12"://0x12:服务器控制设备状态
//                break;
            case "0x13"://0x13:心跳包


                break;
        }

    }


    /**
     * 像mcu  端 发送消息
     *
     * @param expression key
     * @param isToken
     */
    private void sendMessage(String expression, boolean isToken) {
        try {
            String resp;
            if (isToken) {
                resp = "55 AA 14 10 00 07";
            } else {
                resp = "55 AA 14 10 FF 07";
            }

            ChannelClient client = ChannelPool.getChannelClient(expression);
            byte[] req = resp.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
            writeBuffer.put(req);
            writeBuffer.flip();
            client.getChannel().write(writeBuffer);
            if (!writeBuffer.hasRemaining()) {
                System.out.println("向客戶端发送消息：" + resp);
                log.info("向客戶端发送消息：" + resp);
            } else {
                writeBuffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.finest("socket--消息發送異常-->" + e.getMessage());
        }
    }


}
