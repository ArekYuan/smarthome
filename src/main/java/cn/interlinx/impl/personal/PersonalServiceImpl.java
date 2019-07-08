package cn.interlinx.impl.personal;

import cn.interlinx.dao.FeedbackMapper;
import cn.interlinx.dao.KdInfoMapper;
import cn.interlinx.entity.Feedback;
import cn.interlinx.entity.KD_Info;
import cn.interlinx.service.personal.PersonalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonalServiceImpl implements PersonalService {

    @Autowired
    KdInfoMapper mapper;

    @Autowired
    FeedbackMapper fMapper;


    @Override
    public KD_Info selectByKdName(String kdName) {
        return mapper.selectByKdName(kdName);
    }

    @Override
    public int addFeedBack(Feedback feedback) {
        return fMapper.insert(feedback);
    }
}
