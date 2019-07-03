package cn.interlinx.controller.device;

import cn.interlinx.entity.Device;
import cn.interlinx.entity.Userinfo;
import cn.interlinx.iot.ChannelClient;
import cn.interlinx.iot.ChannelPool;
import cn.interlinx.service.intel.DeviceService;
import cn.interlinx.service.login.LoginService;
import cn.interlinx.utils.util.HexUtil;
import cn.interlinx.utils.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

@RestController
public class DeviceController {

    @Autowired
    DeviceService service;

    @Autowired
    LoginService loginService;

    String jsonStr;


    /**
     * 用户绑定设备 通过钱端 传过来的userID 去数据库查找该用户信息（mac地址）
     * 并且去遍历device 列表，和其中的mac地址一一比对，如果相同则绑定在一起
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "api/device/connect")
    public String connectDevice(Integer userId) {
        Userinfo userinfo = loginService.selectByUserId(userId);
        if (userinfo != null) {
            String wifiMac = userinfo.getMac();
            List<Device> devices = service.selectAll();
            if (devices != null && devices.size() > 0) {
                for (Device device : devices) {
                    if (device.getWifi_mac().equals(wifiMac)) {
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
    @RequestMapping(value = "api/device/selectAllDevice")
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
    @RequestMapping(value = "/api/device/bindDevice", method = RequestMethod.POST)
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
    @RequestMapping(value = "/api/device/unBindDevice", method = RequestMethod.GET)
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
    @RequestMapping(value = "/api/device/findDeviceById", method = RequestMethod.GET)
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
    @RequestMapping(value = "/api/device/fndAllDevice", method = RequestMethod.GET)
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
    @RequestMapping(value = "/api/device/updateDevice", method = RequestMethod.POST)
    public String upDateDevice(Device device) throws IOException {
        Device device1 = service.selectId(device.getDeviceId());
        if (device1 != null) {
            String mac = device.getDevice_mac();
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
                    boolean isSuccess = initData(list);
                    if (isSuccess) {
                        int flag = service.updateDevice(device);
                        if (flag == 1) {
                            jsonStr = ResponseUtils.getResult("200", "设置成功", "1");
                        } else {
                            jsonStr = ResponseUtils.getResult("500", "设置失败，服务器异常", "");
                        }
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

}
