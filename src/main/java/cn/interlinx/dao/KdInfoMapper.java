package cn.interlinx.dao;

import cn.interlinx.entity.KD_Info;
import org.springframework.stereotype.Repository;

@Repository
public interface KdInfoMapper {

    KD_Info selectByKdName(String kdName);

}
