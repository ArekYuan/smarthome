package cn.interlinx.controller.device;

import cn.interlinx.entity.Device;
import cn.interlinx.entity.Userinfo;
import cn.interlinx.iot.ChannelClient;
import cn.interlinx.iot.ChannelPool;
import cn.interlinx.iot.Server;
import cn.interlinx.service.intel.DeviceService;
import cn.interlinx.service.login.LoginService;
import cn.interlinx.utils.util.HexUtil;
import cn.interlinx.utils.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class DeviceController {

    @Autowired
    DeviceService service;

    @Autowired
    LoginService loginService;

    String jsonStr;

    private Logger log = Logger.getLogger(DeviceController.class.getSimpleName());

    /**
     * 用户绑定设备 通过钱端 传过来的userID 去数据库查找该用户信息（mac地址）
     * 并且去遍历device 列表，和其中的mac地址一一比对，如果相同则绑定在一起
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/api/connect")
    public String connectDevice(Integer userId) {
        Userinfo userinfo = loginService.selectByUserId(userId);
        if (userinfo != null) {
            String wifiMac = userinfo.getMac();
            List<Device> devices = service.selectAll();
            if (devices != null && devices.size() > 0) {
                for (Device device : devices) {
                    if (device.getWifiMac().equals(wifiMac)) {
                        device.setUserid(userId);
                        service.updateDevice(device);
                    }
                }
            }

        } else {
            jsonStr = ResponseUtils.getResult("400", "该用户不存在", "");
        }

        return jsonStr;
    }

    /**
     * 获取所有设备列表
     *
     * @return String
     */
    @RequestMapping(value = "/api/selectAllDevice")
    public String selectAllDevice() {
        List<Device> devices = service.selectAll();
        if (devices != null && devices.size() > 0) {
            jsonStr = ResponseUtils.getResult("200", "设备列表获取成功", devices);
        } else {
            jsonStr = ResponseUtils.getResult("400", "设备列表为空", "");
        }
        return jsonStr;
    }

    /**
     * 绑定一台设备
     *
     * @param device 设备信息
     * @return
     */
    @RequestMapping(value = "/api/bindDevice", method = RequestMethod.POST)
    public String bindDevice(Device device) {
        Userinfo userinfo = loginService.selectByUserId(device.getUserid());
        if (userinfo != null) {
            int flag = service.insert(device);
            if (flag == 1) {
                jsonStr = ResponseUtils.getResult("200", "新增成功", "1");
            } else {
                jsonStr = ResponseUtils.getResult("500", "新增失败，服务器异常", "");
            }
        } else {
            jsonStr = ResponseUtils.getResult("400", "该用户不存在", "");
        }

        return jsonStr;
    }

    /**
     * 解绑单个设备
     *
     * @param id 设备id
     * @return
     */
    @RequestMapping(value = "/api/unBindDevice", method = RequestMethod.GET)
    public String unBindDevice(@RequestParam("id") Integer id) {

        Device device = service.selectId(id);
        if (device != null) {
            int flag = service.deleteById(id);
            if (flag == 1) {
                jsonStr = ResponseUtils.getResult("200", "解綁成功", "1");
            } else {
                jsonStr = ResponseUtils.getResult("500", "解綁失败，服务器异常", "");
            }
        } else {
            jsonStr = ResponseUtils.getResult("400", "该设备不存在，请稍候重试", "");
        }


        return jsonStr;
    }

    /**
     * 设备详情
     *
     * @param id 设备id
     * @return
     */
    @RequestMapping(value = "/api/findDeviceById", method = RequestMethod.GET)
    public String findDeviceById(@RequestParam("id") Integer id) {
        Device device = service.selectId(id);
        if (device != null) {
            jsonStr = ResponseUtils.getResult("200", "获取成功", device);
        } else {
            jsonStr = ResponseUtils.getResult("500", "没有找到该设备", "");
        }
        return jsonStr;
    }

    /**
     * 设备列表
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/api/fndAllDevice", method = RequestMethod.GET)
    public String findAllDevice(@RequestParam("userId") Integer id) {
        List<Device> deviceList = service.selectAll(id);
        if (deviceList != null && deviceList.size() > 0) {
            jsonStr = ResponseUtils.getResult("200", "获取成功", deviceList);
        } else {
            jsonStr = ResponseUtils.getResult("500", "数据为空", "");
        }
        return jsonStr;

    }

    /**
     * 配置设备参数
     *
     * @param device
     * @return
     */
    @RequestMapping(value = "/api/updateDevice", method = RequestMethod.POST)
    public String upDateDevice(Device device) throws IOException {
        Device device1 = service.selectId(device.getDeviceId());
        if (device1 != null) {
            String mac = device.getWifiMac();
            ChannelClient client = ChannelPool.getChannelClient(mac);
            if (client != null) {
                String hex = getData(device);//拼接成16进制字符串
                byte[] b = HexUtil.hexStringToBytes("");
                ByteBuffer writeBuffer = ByteBuffer.allocate(b.length);
                writeBuffer.put(b);
                writeBuffer.flip();
                client.getChannel().write(writeBuffer);
                if (writeBuffer.hasRemaining()) {
                    writeBuffer.clear();
                }

                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                //读取请求码流，返回读取到的字节数
                int readBytes = client.getChannel().read(byteBuffer);
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
//                    System.out.println("--mcu-通讯码->" + Arrays.toString(a));
//                    log.info("--mcu-通讯码->" + Arrays.toString(a));
//                    String sum = getSun(datas);
                    String data = HexUtil.getMcuData(datas);
//                    System.out.println("----sun-->" + sum);
                    String he = datas.get(datas.size() - 1);


//                    String mcuData = HexUtil.getMcuData(list);
                    switch (data) {
                        case "0x11"://设备上报
                            int flag = service.updateDevice(device);
                            if (flag == 1) {
                                jsonStr = ResponseUtils.getResult("200", "设置成功", "1");
                            } else {
                                jsonStr = ResponseUtils.getResult("500", "设置失败，服务器异常", "");
                            }
                            break;
                        case "0x12"://服务器控制设备状态
                            break;

                    }
                    if (data.equals("0x11")) {//

                    } else {
                        jsonStr = ResponseUtils.getResult("4005", "该设备或已掉线，请稍候重试", "");
                    }
                }

            } else {
                jsonStr = ResponseUtils.getResult("4006", "该设备不存在，请稍候重试", "");
            }

        } else {
            jsonStr = ResponseUtils.getResult("4006", "该设备不存在，请稍候重试", "");
        }

        return jsonStr;
    }

    /**
     * 拼接
     *
     * @param device
     * @return
     */
    private String getData(Device device) {
        return null;
    }

    /**
     * 设备返回 服务器 处理逻辑
     *
     * @param strList
     * @return
     */
    private boolean initData(List<String> strList) {
        boolean b = false;
        if (strList != null && strList.size() > 0) {
            if (strList.get(0).equals("55") && strList.get(1).equals("AA")) {
                int len = Integer.parseInt(strList.get(2), 16);//将16进制数转成10进制数
                if (len == strList.size()) {
                    switch (strList.get(3)) {
                        case "12"://0x12:服务器控制设备状态
                            b = true;
                            break;
                        default:
                            b = false;
                            break;
                    }
                }

            }
        }
        return b;
    }

    /**
     * 打开 mcu 设备
     *
     * @param key 用户传给的值
     * @return
     */
    @RequestMapping("/api/openMcu")
    public String openMcu(String key, int open) {

        Device device = getDevice(key);
        String tag = "mode";
//        String resp = "";
        byte[] resp = new byte[0];
        if (device != null) {
            ChannelClient client = ChannelPool.getChannelClient(key);
            if (client != null) {
                String[] tr;
                if (open == 0) {//关闭设备
                    device.setTakeOff(0);
                    tr = new String[]{"55", "AA", "08", "12", "00", "01", "00"};
                    String sun = HexUtil.getSun(tr);
//                    resp = "0x55 0xAA 0x08 0x12 0x00 0x01 0x00 " + "0x" + sun + "";
                    int s1 = Integer.parseInt(sun, 16);
                    resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) s1};

                } else if (open == 1) {// 打开设备
                    device.setTakeOff(1);
                    tr = new String[]{"55", "AA", "08", "12", "00", "01", "01"};
                    String sun = HexUtil.getSun(tr);
                    int s1 = Integer.parseInt(sun, 16);
                    resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) s1};
                }

                int flag = sendMsg(device, resp, tag, client);
                if (flag == 1) {
                    jsonStr = ResponseUtils.getResult("200", "打开设备成功", device.getMode() + "");
                } else {
                    jsonStr = ResponseUtils.getResult("500", "打开设备失败", "-1");
                }
            }
        } else {
            jsonStr = ResponseUtils.getResult("4001", "设备不存在", "");
        }


        return jsonStr;
    }

    /**
     * @param key  通道key
     * @param mode 模式 0：连续运转模式  1：智能模式
     * @return
     */
    @RequestMapping("/api/intelMode")
    public String getIntelMode(String key, int mode) {
        Device device = getDevice(key);
        String tag = "mode";
        byte[] resp = new byte[0];
        if (device != null) {
            ChannelClient client = ChannelPool.getChannelClient(key);
            String[] tr;
            if (client != null) {
                if (mode == 0) {//连续运转模式
                    device.setMode(0);
                    tr = new String[]{"55", "AA", "08", "12", "01", "01", "00"};
                    String sun = HexUtil.getSun(tr);
//                    resp = "0x55 0xAA 0x08 0x12 0x01 0x01 0x00 " + "0x" + sun + "";
                    int s1 = Integer.parseInt(sun, 16);
                    resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) s1};


                } else if (mode == 1) {// 智能模式
                    device.setMode(1);
                    tr = new String[]{"55", "AA", "08", "12", "01", "01", "01"};
                    String sun = HexUtil.getSun(tr);
//                    resp = "0x55 0xAA 0x08 0x12 0x01 0x01 0x01 " + "0x" + sun + "";

                    int s1 = Integer.parseInt(sun, 16);
                    resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) s1};

                }

                int flag = sendMsg(device, resp, tag, client);
                if (flag == 1) {
                    jsonStr = ResponseUtils.getResult("200", "智能能模式设置成功", device.getMode() + "");
                } else {
                    jsonStr = ResponseUtils.getResult("500", "智能能模式控制失败", "-1");
                }
            }
        } else {
            jsonStr = ResponseUtils.getResult("4001", "设备不存在", "");
        }

        return jsonStr;
    }


    /**
     * 键盘锁
     *
     * @param key  通道key 值
     * @param lock 0：解锁  1：上锁
     * @return
     */
    @RequestMapping("/api/inputLock")
    public String getInputLock(String key, int lock) {
        Device device = getDevice(key);
        String tag = "lock";
        byte[] resp = new byte[0];
        ChannelClient client = ChannelPool.getChannelClient(key);
        if (client != null) {
            String[] tr;
            if (lock == 0) {//解锁
                device.setLock(0);
                tr = new String[]{"55", "AA", "08", "12", "02", "01", "00"};
                String sun = HexUtil.getSun(tr);
//                resp = "0x55 0xAA 0x08 0x12 0x02 0x01 0x00 " + "0x" + sun + "";

                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x02, (byte) 0x01, (byte) 0x00, (byte) s1};


            } else if (lock == 1) {// 上锁
                device.setLock(1);
                tr = new String[]{"55", "AA", "08", "12", "02", "01", "01"};
                String sun = HexUtil.getSun(tr);
//                resp = "0x55 0xAA 0x08 0x12 0x02 0x01 0x01 " + "0x" + sun + "";
                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x02, (byte) 0x01, (byte) 0x01, (byte) s1};

            }

            int flag = sendMsg(device, resp, tag, client);
            if (flag == 1) {
                jsonStr = ResponseUtils.getResult("200", "设置设备锁成功", device.getLock() + "");
            } else {
                jsonStr = ResponseUtils.getResult("500", "设置设备锁失败", "-1");
            }

        }

        return jsonStr;
    }


    /**
     * 风力强度
     *
     * @param key
     * @param strong 01 最弱,02 中,03 最强
     * @return
     */
    @RequestMapping("/api/winStrong")
    public String getWinStrong(String key, int strong) {
        Device device = getDevice(key);
        String tag = "strong";
        byte[] resp = new byte[0];
        ChannelClient client = ChannelPool.getChannelClient(key);
        if (client != null) {
            String[] tr;
            if (strong == 1) {//最弱
                device.setWindPower(1);
                tr = new String[]{"55", "AA", "08", "12", "03", "01", "01"};
                String sun = HexUtil.getSun(tr);
//                resp = "0x55 0xAA 0x08 0x12 0x03 0x01 0x01 " + "0x" + sun + "";

                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x03, (byte) 0x01, (byte) 0x01, (byte) s1};


            } else if (strong == 2) {// 中
                device.setWindPower(2);
                tr = new String[]{"55", "AA", "08", "12", "03", "01", "02"};
                String sun = HexUtil.getSun(tr);
//                resp = "0x55 0xAA 0x08 0x12 0x03 0x01 0x02 " + "0x" + sun + "";

                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x03, (byte) 0x01, (byte) 0x02, (byte) s1};

            } else if (strong == 3) {//最强
                device.setWindPower(3);
                tr = new String[]{"55", "AA", "08", "12", "03", "01", "03"};
                String sun = HexUtil.getSun(tr);
//                resp = "0x55 0xAA 0x08 0x12 0x03 0x01 0x03 " + "0x" + sun + "";
                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x03, (byte) 0x01, (byte) 0x03, (byte) s1};

            }

            int flag = sendMsg(device, resp, tag, client);
            if (flag == 1) {
                jsonStr = ResponseUtils.getResult("200", "设置风力强度成功", device.getWindPower() + "");
            } else {
                jsonStr = ResponseUtils.getResult("500", "设置风力强度失败", "-1");
            }

        }

        return jsonStr;
    }

    /**
     * 氛围灯
     *
     * @param key
     * @param light 0 关闭，01 最弱,02 中,03 最强
     * @return
     */
    @RequestMapping("/api/light")
    public String getLight(String key, int light) {

        Device device = getDevice(key);
        String tag = "light";
//        String resp = "";
        byte[] resp = new byte[0];
        ChannelClient client = ChannelPool.getChannelClient(key);
        if (client != null) {
            String[] tr;

            if (light == 0) {//关闭
                device.setAirLamp(0);
                tr = new String[]{"55", "AA", "08", "12", "04", "01", "00"};
                String sun = HexUtil.getSun(tr);
                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x04, (byte) 0x01, (byte) 0x00, (byte) s1};
            } else if (light == 1) {// 最弱
                device.setAirLamp(1);
                tr = new String[]{"55", "AA", "08", "12", "04", "01", "01"};
                String sun = HexUtil.getSun(tr);
                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x04, (byte) 0x01, (byte) 0x01, (byte) s1};
            } else if (light == 2) {//中
                device.setAirLamp(2);
                tr = new String[]{"55", "AA", "08", "12", "04", "01", "02"};
                String sun = HexUtil.getSun(tr);
                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x04, (byte) 0x01, (byte) 0x02, (byte) s1};
            } else if (light == 3) {//最强
                device.setAirLamp(3);
                tr = new String[]{"55", "AA", "08", "12", "04", "01", "03"};
                String sun = HexUtil.getSun(tr);
                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x04, (byte) 0x01, (byte) 0x03, (byte) s1};
            }

            int flag = sendMsg(device, resp, tag, client);
            if (flag == 1) {
                jsonStr = ResponseUtils.getResult("200", "设置氛围灯成功", device.getAirLamp() + "");
            } else {
                jsonStr = ResponseUtils.getResult("500", "设置氛围灯失败", "-1");
            }

        }

        return jsonStr;

    }

    /**
     * 定时设置
     *
     * @param key
     * @param timeSt 0/ 无定时，01/3小时,02/6小时,03/12小时
     * @return
     */
    @RequestMapping("/api/timeSt")
    public String getTimeSet(String key, int timeSt) {

        Device device = getDevice(key);
        String tag = "timeSt";
        byte[] resp = new byte[0];

        ChannelClient client = ChannelPool.getChannelClient(key);
        if (client != null) {
            String[] tr;
            if (timeSt == 0) {//无定时
                device.setTimingSet(0);
                tr = new String[]{"55", "AA", "08", "12", "05", "01", "00"};
                String sun = HexUtil.getSun(tr);
//                resp = "0x55 0xAA 0x08 0x12 0x05 0x01 0x00 " + "0x" + sun + "";

                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x05, (byte) 0x01, (byte) 0x00, (byte) s1};


            } else if (timeSt == 1) {// 3小时
                device.setTimingSet(1);
                tr = new String[]{"55", "AA", "08", "12", "05", "01", "01"};
                String sun = HexUtil.getSun(tr);
//                resp = "0x55 0xAA 0x08 0x12 0x05 0x01 0x01 " + "0x" + sun + "";
                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) s1};

            } else if (timeSt == 2) {//6小时
                device.setTimingSet(2);
                tr = new String[]{"55", "AA", "08", "12", "05", "01", "02"};
                String sun = HexUtil.getSun(tr);
//                resp = "0x55 0xAA 0x08 0x12 0x05 0x01 0x02 " + "0x" + sun + "";

                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x05, (byte) 0x01, (byte) 0x02, (byte) s1};


            } else if (timeSt == 3) {//12小时
                device.setTimingSet(3);
                tr = new String[]{"55", "AA", "08", "12", "05", "01", "03"};
                String sun = HexUtil.getSun(tr);
//                resp = "0x55 0xAA 0x08 0x12 0x05 0x01 0x03 " + "0x" + sun + "";

                int s1 = Integer.parseInt(sun, 16);
                resp = new byte[]{(byte) 0x55, (byte) 0xAA, (byte) 0x08, (byte) 0x12, (byte) 0x05, (byte) 0x01, (byte) 0x03, (byte) s1};


            }

            int flag = sendMsg(device, resp, tag, client);
            if (flag == 1) {
                jsonStr = ResponseUtils.getResult("200", "设置定时成功", device.getTimingSet() + "");
            } else {
                jsonStr = ResponseUtils.getResult("500", "设置定时失败", "-1");
            }

        }
        return jsonStr;
    }


    private int sendMsg(Device device, byte[] resp, String tag, ChannelClient client) {
        int flag = -1;
        try {
//            byte[] req = resp.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(resp.length);
            writeBuffer.put(resp);
            writeBuffer.flip();
            client.getChannel().write(writeBuffer);
            if (!writeBuffer.hasRemaining()) {//向设备发送消息回调
                switch (tag) {
                    case "open"://开关
                    case "mode"://模式
                    case "lock"://键盘锁
                    case "strong"://风力强度
                    case "light"://氛围灯
                    case "timeSt"://定时
                        flag = service.updateDeviceId(device);
                        break;
                }
                System.out.println("向客戶端发送消息：" + resp);
                log.info("向客戶端发送消息：" + resp);
            } else {
                writeBuffer.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.finest("socket--消息发送异常-->" + e.getMessage());
            flag = -1;
        }

        return flag;
    }

    public Device getDevice(String key) {
        return service.selectByKey(key);
    }

    public int insertDevice(Device device) {
        return service.insert(device);
    }

}
