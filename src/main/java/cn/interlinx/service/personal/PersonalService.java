package cn.interlinx.service.personal;

import cn.interlinx.entity.Feedback;
import cn.interlinx.entity.KD_Info;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PersonalService {
    KD_Info selectByKdName(String kdName);

    int addFeedBack(Feedback feedback);

}
