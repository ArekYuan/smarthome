package cn.interlinx.service.intel;


import cn.interlinx.entity.Device;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IntelService {

    /**
     * 用户绑定一台机器
     *
     * @param device 设备
     * @return
     */
    int addNewDevice(String userId, Device device);

    /**
     * 用户远程开关机
     *
     * @param take_off 开关-->0：关  1：开
     * @return
     */
    int setTakeOff(String userId, String deviceId, String take_off);

    /**
     * 远程设置 风力
     *
     * @param wind_power 风力强度-->1：弱，2：中，3：强
     * @return
     */
    int setWindPower(String userId, String deviceId, String wind_power);

    /**
     * 远程设置 氛围灯强度
     *
     * @param air_lamp 氛围灯-->0：关闭，1：弱，2：中，3：强
     * @return
     */
    int setAirLamp(String userId, String deviceId, String air_lamp);

    /**
     * 远程切换工作模式
     *
     * @param mode 智能模式-->0：连续运转模式  1：智能模式
     * @return
     */
    int modeDevice(String userId, String deviceId, String mode);

    /**
     * 设备上锁
     *
     * @param lock 键盘锁-->0：解锁  1：上锁
     * @return
     */
    int lockDevice(String userId, String deviceId, String lock);

    /**
     * 设置 设备，
     *
     * @param userId    用户id
     * @param deviceIds 设备id
     * @param lock      设置参数
     * @return
     */
    int setDevices(String userId, String deviceIds, String lock);


    /**
     * 获取 所有设备
     *
     * @param userId 用户id
     * @return
     */
    List<Device> getAllDevice(String userId);


}
