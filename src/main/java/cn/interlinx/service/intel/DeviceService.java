package cn.interlinx.service.intel;

import cn.interlinx.entity.Device;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface DeviceService {

    /**
     * 解綁设备
     *
     * @param id
     * @return
     */
    int deleteById(Integer id);

    /**
     * 新增一台设备
     *
     * @param record
     * @return
     */
    int insert(Device record);

    /**
     * 查找单个设备
     *
     * @param id
     * @return
     */
    Device selectId(Integer id);

    /**
     * 遍历所有的设备
     *
     * @return
     */
    List<Device> selectAll(Integer id);

    /**
     * 修改设备的属性值
     *
     * @param record
     * @return
     */
    int updateDevice(Device record);


    /**
     * 查询所有设备
     *
     * @return
     */
    List<Device> selectAll();

}
