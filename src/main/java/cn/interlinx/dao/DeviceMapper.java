package cn.interlinx.dao;

import cn.interlinx.entity.Device;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DeviceMapper {
    int deleteByPrimaryKey(Integer deviceId);

    int insert(Device record);

    Device selectByPrimaryKey(Integer deviceId);

    List<Device> selectAll(Integer id);

    int updateByPrimaryKey(Device record);

    List<Device> selectAll();

    Device selectByDeviceKey(String key);
}