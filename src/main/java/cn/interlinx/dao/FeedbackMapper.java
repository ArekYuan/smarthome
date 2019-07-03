package cn.interlinx.dao;

import cn.interlinx.entity.Feedback;
import java.util.List;

public interface FeedbackMapper {
    int deleteByPrimaryKey(Integer feedbackId);

    int insert(Feedback record);

    Feedback selectByPrimaryKey(Integer feedbackId);

    List<Feedback> selectAll();

    int updateByPrimaryKey(Feedback record);
}