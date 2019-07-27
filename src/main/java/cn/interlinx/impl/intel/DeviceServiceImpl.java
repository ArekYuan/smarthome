package cn.interlinx.impl.intel;

import cn.interlinx.dao.DeviceMapper;
import cn.interlinx.entity.Device;
import cn.interlinx.service.intel.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    DeviceMapper mapper;

    @Override
    public int deleteById(Integer id) {
        return mapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(Device record) {
        return mapper.insert(record);
    }

    @Override
    public Device selectId(Integer id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Device> selectAll(Integer id) {
        return mapper.selectAll(id);
    }

    @Override
    public int updateDevice(Device record) {
        return mapper.updateByPrimaryKey(record);
    }

    @Override
    public List<Device> selectAll() {
        return mapper.selectAll();
    }

    @Override
    public Device selectByKey(String key) {
        return mapper.selectByDeviceKey(key);
    }

    @Override
    public int updateDeviceId(Device record) {
        return mapper.updateDeviceId(record);
    }
}
