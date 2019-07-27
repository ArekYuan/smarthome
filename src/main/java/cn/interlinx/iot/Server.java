package cn.interlinx.iot;

import cn.interlinx.controller.device.DeviceController;
import cn.interlinx.entity.Device;
import cn.interlinx.entity.DeviceValue;
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
import java.util.*;
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
                            List<String> list = HexUtil.bytes2HexString(bytes);
                            List<String> datas = HexUtil.getMcuDataList(list);
                            String[] a = list.toArray(new String[datas.size()]);
                            String sum = getSun(datas);
                            String data = HexUtil.getMcuData(datas);
                            String he = datas.get(datas.size() - 1);
                            boolean isToken = false;//求和 并提示
                            if (he.equals(sum)) {//验证成功
                                isToken = true;
                                System.out.println("--mcu-通讯码->" + Arrays.toString(a));
                                log.info("--mcu-通讯码->" + Arrays.toString(a));

                                switch (data) {
                                    case "0x10"://设备登录到服务器
                                        int chang_id = getChangId(list);
                                        int sheBei_id = getSBId(list);
                                        String mac = getMac(list);
                                        String key = String.valueOf(chang_id + sheBei_id);
                                        Device device = service.selectByKey(key);
                                        if (device != null && device.getWifiMac() != null) {
                                            expression = key;
                                        } else {
                                            expression = key;
                                            Device device1 = new Device();
                                            device1.setWifiMac(mac);
                                            device1.setDeviceKey(key);
                                            service.insert(device1);
                                        }

                                        list.clear();
                                        a = null;
                                        break;
                                    case "0x11"://设备状态上报
                                        List<String> baoDataList = getDataLen(list);
                                        String key1 = ChannelPool.getKey(socketChannel);
                                        expression = key1;
                                        if (key1 != null && !key1.equals("")) {
                                            Device device1 = service.selectByKey(key1);
                                            Device device2 = upDataDevice(device1, baoDataList);
                                            int flag = service.updateDeviceId(device2);
                                            if (flag == 1) {
                                                System.out.println("修改成功");
                                            } else {
                                                System.out.println("修改失败");
                                            }
                                        }
                                        list.clear();
                                        a = null;
                                        break;
                                    case "0x12"://服务器控制设备状态
                                        String key2 = ChannelPool.getKey(socketChannel);
                                        expression = key2;
                                        list.clear();
                                        a = null;
                                        break;
                                    case "0x13"://心跳包
                                        String key3 = ChannelPool.getKey(socketChannel);
                                        expression = key3;
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
     * 更新设备 数值
     *
     * @param device1 设备
     * @return
     */
    private Device upDataDevice(Device device1, List<String> baoDataList) {

        List<List<String>> datas = new ArrayList<>();
        if (baoDataList.size() % 3 == 0) {
            int i = baoDataList.size() / 3;//共有几个等分

            for (int j = 0; j <= i - 1; j++) {//分割这个数组
                if (j == 0) {
                    List<String> lists = baoDataList.subList(j, 3);
                    datas.add(lists);
                } else if (j == i - 1) {
                    List<String> lists = baoDataList.subList(3 * j, baoDataList.size());
                    datas.add(lists);
                } else {
                    List<String> lists = baoDataList.subList(3 * j, 3 * j + 3);
                    datas.add(lists);
                }


            }
        }

        if (datas.size() > 0) {
            List<DeviceValue> valueList = new ArrayList<>();
            for (List<String> data : datas) {
                DeviceValue value = new DeviceValue();
                value.setID("0x" + data.get(0));
                value.setLen(data.get(1));
                value.setValue(data.get(2));
                valueList.add(value);
            }


            if (valueList.size() > 0) {
                for (DeviceValue value : valueList) {
                    switch (value.getID()) {
                        case "0x00"://开关
                            if (value.getValue().equals("00")) {//关闭
                                device1.setTakeOff(0);
                            } else if (value.getValue().equals("01")) {//打开
                                device1.setTakeOff(1);
                            }
                            break;
                        case "0x01"://智能模式
                            if (value.getValue().equals("00")) {//连续运转模式
                                device1.setMode(0);
                            } else if (value.getValue().equals("01")) {//智能模式
                                device1.setMode(1);
                            }
                            break;
                        case "0x02"://键盘锁
                            if (value.getValue().equals("00")) {//解锁
                                device1.setLock(0);
                            } else if (value.getValue().equals("01")) {//上锁
                                device1.setLock(1);
                            }
                            break;
                        case "0x03"://风力强度
                            if (value.getValue().equals("01")) {//最弱
                                device1.setAirLamp(1);
                            } else if (value.getValue().equals("02")) {//中
                                device1.setAirLamp(2);
                            } else if (value.getValue().equals("03")) {//最强
                                device1.setAirLamp(3);
                            }
                            break;
                        case "0x04"://氛围灯
                            if (value.getValue().equals("00")) {//关闭
                                device1.setAirLamp(1);
                            } else if (value.getValue().equals("01")) {//最弱
                                device1.setAirLamp(2);
                            } else if (value.getValue().equals("02")) {//中
                                device1.setAirLamp(3);
                            } else if (value.getValue().equals("03")) {//最强
                                device1.setAirLamp(3);
                            }
                            break;
                        case "0x05"://定时设置
                            if (value.getValue().equals("00")) {//无定时
                                device1.setTimingSet(0);
                            } else if (value.getValue().equals("01")) {//3小时
                                device1.setTimingSet(1);
                            } else if (value.getValue().equals("02")) {//6小时
                                device1.setTimingSet(2);
                            } else if (value.getValue().equals("03")) {//12小时
                                device1.setTimingSet(3);
                            }
                            break;
                        case "0x06"://水位值
                            int water = Integer.parseInt(value.getValue(), 16);
                            device1.setWaterLevel(water);
                            break;
                    }
                }
            }
        }


        return device1;
    }

    private List<String> getDataLen(List<String> list) {

        return list.subList(5, list.size() - 1);
    }

    /**
     * 数据域 校验和
     *
     * @param list
     * @return
     */
    private String getSun(List<String> list) {
        int sum = 0;
        for (int i = 0; i < list.size() - 2; i++) {
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
                sendMessage(expression, isToken, "0x10");
                break;
            case "0x11"://0x11:设备状态上报
                sendMessage(expression, isToken, "0x11");
                break;
            case "0x12"://0x12:服务器控制设备状态
                sendMessage(expression, isToken, "0x12");
                break;
            case "0x13"://0x13:心跳包
                sendMessage(expression, isToken, "0x13");
                break;
        }

    }


    /**
     * 像mcu  端 发送消息
     *
     * @param key     key
     * @param isToken 数据是否正常
     */
    private void sendMessage(String key, boolean isToken, String flag) {
        String[] tr;
        if (isToken) {//验证通过
            byte[] sends = new byte[0];
            switch (flag) {
                case "0x10"://登录到服务器
                    tr = new String[]{"55", "AA", "07", "10", "00"};
                    String sum = HexUtil.getSun(tr);
                    int s0 = Integer.parseInt(sum, 16);
                    System.out.println("----s0--->" + s0);
                    sends = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x07, (byte) 0x10, (byte) 0x00, (byte) s0};
                    break;
                case "0x11"://0x11:设备状态上报
                    tr = new String[]{"55", "AA", "06", "11"};
                    String sum1 = HexUtil.getSun(tr);
                    int s1 = Integer.parseInt(sum1, 16);
                    sends = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x06, (byte) 0x11, (byte) s1};
                    break;
                case "0x12"://0x12:服务器控制设备状态
//                    resp = "55 AA 06 12 06";
                    break;
                case "0x13"://心跳包
                    tr = new String[]{"55", "AA", "05", "13"};
                    String sum2 = HexUtil.getSun(tr);
                    int s2 = Integer.parseInt(sum2, 16);
                    sends = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x05, (byte) 0x13, (byte) s2};
                    break;
                default:
                    break;
            }
            sendMcuData(sends, key);
        } else {//没有验证通过 则登录失败
            tr = new String[]{"55", "AA", "07", "10", "01"};
            String sum = HexUtil.getSun(tr);
            int s0 = Integer.parseInt(sum, 16);
            System.out.println("----s0--->" + s0);
            byte[] sends = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x07, (byte) 0x10, (byte) 0x01, (byte) s0};
            sendMcuData(sends, key);
        }

    }

    private void sendMcuData(byte[] sends, String key) {
        try {
            ChannelClient client = ChannelPool.getChannelClient(key);
//            byte[] req = sends.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(sends.length);
            writeBuffer.put(sends);
            writeBuffer.flip();
            client.getChannel().write(writeBuffer);
            if (writeBuffer.hasRemaining()) {
//                System.out.println("向客戶端发送消息：" + sends);
//                log.info("向客戶端发送消息：" + sends);
                writeBuffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.finest("socket--消息發送異常-->" + e.getMessage());
        }
    }


    public int getUnsignedByte(byte data) {      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data & 0x0FF;
    }

    public int getUnsignedInt(int data) {     //将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
        return data & 0x0FFFFFFFF;
    }

}
